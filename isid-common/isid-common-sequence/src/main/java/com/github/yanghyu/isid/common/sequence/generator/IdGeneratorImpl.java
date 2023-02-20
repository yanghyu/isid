package com.github.yanghyu.isid.common.sequence.generator;


import com.github.yanghyu.isid.common.core.generator.IdGenerator;
import com.github.yanghyu.isid.common.core.id.IdRandomizer;
import com.github.yanghyu.isid.common.core.message.base.Message;
import com.github.yanghyu.isid.common.core.message.base.Result;
import com.github.yanghyu.isid.common.core.message.base.SysMessage;
import com.github.yanghyu.isid.common.core.message.exception.BizException;
import com.github.yanghyu.isid.common.core.message.exception.SysException;
import com.github.yanghyu.isid.common.sequence.handler.SegmentHandler;
import com.github.yanghyu.isid.common.sequence.model.Segment;
import com.github.yanghyu.isid.common.sequence.model.SegmentQueue;
import com.github.yanghyu.isid.common.sequence.util.LoopUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;

/**
 * ID 生成器
 *
 * @author yanghongyu
 * @since 2020-08-06
 */
public class IdGeneratorImpl implements IdGenerator {

    private static final Logger logger = LoggerFactory.getLogger(IdGeneratorImpl.class);

    /**
     * 最小步长
     */
    private static final int MIN_STEP = 1000;

    /**
     * 最大步长
     */
    private static final int MAX_STEP = 500000;

    /**
     * 加载新分段最大超时时间（5s）
     */
    private static final long LOAD_SEGMENT_MAX_TIMEOUT = 5000L;

    /**
     * 一个分段期望维持时间为15分钟
     */
    private static final long EXPECTED_SEGMENT_DURATION = 900000L;

    /**
     * 分段队列缓存列表
     */
    private final ConcurrentHashMap<String, SegmentQueue> keySegmentQueueMap = new ConcurrentHashMap<>();

    /**
     * 序列段处理器
     */
    private final SegmentHandler segmentHandler;

    /**
     * 异步执行线程池
     */
    private final ExecutorService executorService = new ThreadPoolExecutor(2, Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS, new SynchronousQueue<>(), new LoadThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy());

    public static class LoadThreadFactory implements ThreadFactory {

        private static int num = 0;

        private static synchronized int threadNum() {
            return num++;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "Thread-Segment-Load-" + threadNum());
            thread.setDaemon(true);
            return thread;
        }

    }

    public IdGeneratorImpl(SegmentHandler segmentHandler) {
        this.segmentHandler = segmentHandler;
    }

    @Override
    public Result<Long> generateNumber(String key) {
        Result<Long> numberResult;
        if (key == null || "".equals(key)) {
            key = "default";
        }
        // 1.获取SegmentQueue对象
        SegmentQueue segmentQueue = keySegmentQueueMap.get(key);
        if (segmentQueue == null) {
            SegmentQueue newSegmentQueue = new SegmentQueue();
            segmentQueue = keySegmentQueueMap.putIfAbsent(key, newSegmentQueue);
            if (segmentQueue == null) {
                segmentQueue = newSegmentQueue;
            }
        }

        try {
            // 2.尝试获取ID
            int roll = 0;
            while (true) {
                Segment segment = segmentQueue.peek();
                if (segment != null) {
                    long number = segment.getCurrentNumber().getAndIncrement();
                    long remain = segment.getMaxNumber() - number;
                    if (remain > 0) {
                        numberResult = new Result<>(number);
                        // 判断是否需要触发线程开启加载新的分段
                        if (remain * 2 < segment.getStepSize() && segmentQueue.size() < 2
                                && isSegmentNotLoading(segmentQueue)) {
                            int newStepSize = calculateStep(segment.getStepSize(), segment.getUpdateTimestamp());
                            applyLoadNewSegment(segmentQueue, key, newStepSize);
                        }
                        break;
                    } else {
                        segmentQueue.remove(segment);
                    }
                } else if (segmentQueue.size() < 2 && isSegmentNotLoading(segmentQueue)){
                    applyLoadNewSegment(segmentQueue, key, MIN_STEP);
                }
                roll = LoopUtil.loopStatus(roll, 30);
            }
        } catch (BizException | SysException le) {
            numberResult = new Result<>(le, 0L);
            logger.error("generate id sequence exception, key:{}", key);
            logger.error("generate id sequence exception", le);
        } catch (RuntimeException e) {
            numberResult = new Result<>(SysMessage.SYS_FAIL, 0L);
            logger.error("generate id exception, key:{}", key);
            logger.error("generate id exception", e);
        }
        return numberResult;
    }

    @Override
    public Result<String> generateId(String key) {
        Result<Long> r = generateNumber(key);
        String id = null;
        if (Message.SUCCESS.getCode().equals(r.getCode())) {
            id = IdRandomizer.randomize(r.getData());
        }
        Result<String> result = new Result<>();
        result.setCode(r.getCode());
        result.setDesc(r.getDesc());
        result.setData(id);
        return result;
    }

    /**
     * 计算步长
     * @param currentStepSize 当前步长
     * @param lastLoadTimestamp 上次加载时间戳
     * @return 计算出的步长
     */
    private int calculateStep(int currentStepSize, long lastLoadTimestamp) {
        int newStepSize = currentStepSize;
        long intervalMillis = System.currentTimeMillis() - lastLoadTimestamp;
        // 该段上次加载距此刻时间差，为了防止此值过小，设置下限为期望时长的百分之一。
        intervalMillis = Math.max(intervalMillis, EXPECTED_SEGMENT_DURATION / 100);
        if (intervalMillis < EXPECTED_SEGMENT_DURATION / 2) {
            newStepSize = (int) (currentStepSize * (Math.round((double) (EXPECTED_SEGMENT_DURATION / intervalMillis)) + 1));
        } else if (intervalMillis < EXPECTED_SEGMENT_DURATION) {
            newStepSize = currentStepSize * 2;
        } else if (intervalMillis > EXPECTED_SEGMENT_DURATION * 2) {
            newStepSize = currentStepSize / 2;
        }
        if (newStepSize < MIN_STEP) {
            newStepSize = MIN_STEP;
        } else if (newStepSize > MAX_STEP) {
            newStepSize = MAX_STEP;
        }
        logger.info("calculate new step size, current step size:{} interval millis:{} new step size:{}", currentStepSize, intervalMillis, newStepSize);
        return newStepSize;
    }

    /**
     * 判断Segment加载状态
     *
     * @param segmentQueue 分段对列
     * @return 是否不在加载中
     */
    private boolean isSegmentNotLoading(SegmentQueue segmentQueue) {
        Lock loadReadLock = segmentQueue.getLoadReadLock();
        loadReadLock.lock();
        boolean loading = false;
        try {
            if (segmentQueue.isLoading()) {
                loading = System.currentTimeMillis() - segmentQueue.getLoadStartTimeMillis() <= LOAD_SEGMENT_MAX_TIMEOUT;
            }
        } finally {
            loadReadLock.unlock();
        }
        return !loading;
    }

    /**
     * 申请加载新的Segment
     *
     * @param segmentQueue 分段队列
     * @param key 键
     * @param stepSize 步长
     */
    private void applyLoadNewSegment(SegmentQueue segmentQueue, String key, int stepSize) {
        Lock loadWriteLock = segmentQueue.getLoadWriteLock();
        loadWriteLock.lock();
        try {
            if (segmentQueue.isLoading()) {
                if (System.currentTimeMillis() - segmentQueue.getLoadStartTimeMillis() > LOAD_SEGMENT_MAX_TIMEOUT) {
                    segmentQueue.setLoading(false);
                }
            }
            if (!segmentQueue.isLoading()) {
                segmentQueue.setLoading(true);
                segmentQueue.setLoadStartTimeMillis(System.currentTimeMillis());
                logger.info("apply load segment, key:{} stepSize:{}", key, stepSize);
                executorService.execute(() -> {
                    try {
                        // 这里在逻辑上不一定能够成功加载回一个分段
                        Segment segment = segmentHandler.getSegment(key, stepSize);
                        if (segment != null) {
                            logger.info("load segment success, {}", segment);
                            segmentQueue.offer(segment);
                        } else {
                            logger.warn("load segment fail, key:{} stepSize:{}", key, stepSize);
                        }
                    } catch (RuntimeException e) {
                        logger.error("load segment exception, key:{} stepSize:{}", key, stepSize);
                        logger.error("load segment exception", e);
                    } finally {
                        Lock taskLoadWriteLock = segmentQueue.getLoadWriteLock();
                        taskLoadWriteLock.lock();
                        segmentQueue.setLoading(false);
                        taskLoadWriteLock.unlock();
                    }
                });
            }
        } catch (RuntimeException e) {
            logger.error("apply load segment exception, key:{} stepSize:{}", key, stepSize);
            logger.error("apply load segment exception", e);
            throw e;
        } finally {
            loadWriteLock.unlock();
        }
    }

}
