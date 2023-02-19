package com.github.yanghyu.isid.common.sequence.handler;



import com.github.yanghyu.isid.common.sequence.dao.SequenceDao;
import com.github.yanghyu.isid.common.sequence.message.SequenceSysMessage;
import com.github.yanghyu.isid.common.sequence.model.Segment;
import com.github.yanghyu.isid.common.sequence.model.Sequence;
import com.github.yanghyu.isid.common.sequence.util.LoopUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 序列段处理器
 *
 * @author yanghongyu
 * @since 2020-08-25
 */
public class SegmentHandler {

    private static final Logger logger = LoggerFactory.getLogger(SegmentHandler.class);

    private final SequenceDao sequenceDao;

    public SegmentHandler(DataSource dataSource) {
        this.sequenceDao = new SequenceDao(dataSource);
    }

    public Segment getSegment(String key, int stepSize) {
        int updateCount, roll = 0;
        Sequence sequence;
        do {
            roll = LoopUtil.loopStatus(roll);
            sequence = getSequence(key);
            sequence.setCurrentNumber(sequence.getCurrentNumber() + stepSize);
            sequence.setUpdateDatetime(LocalDateTime.now());
            sequence.setVersion(sequence.getVersion() + 1);
            updateCount = sequenceDao.updateMaxId(sequence);
        } while (updateCount < 1);
        Segment segment = new Segment();
        segment.setKey(key);
        segment.setMaxNumber(sequence.getCurrentNumber());
        segment.setCurrentNumber(new AtomicLong(sequence.getCurrentNumber() - stepSize));
        segment.setStepSize(stepSize);
        segment.setUpdateTimestamp(System.currentTimeMillis());
        return segment;
    }

    public Sequence getSequence(String key) {
        int roll = 0;
        Sequence sequence;
        do {
            roll = LoopUtil.loopStatus(roll);
            sequence = sequenceDao.get(key);
            if (sequence == null) {
                synchronized (this) {
                    sequence = sequenceDao.get(key);
                    if (sequence == null) {
                        try {
                            sequence = insertKey(key, null);
                        } catch (RuntimeException e) {
                            // do nothing
                        }
                    }
                }
            }
        } while (sequence == null);
        return sequence;
    }

    public Sequence insertKey(String key, Integer stepSize) {
        Sequence sequence = new Sequence();
        sequence.setKey(key);
        LocalDateTime now = LocalDateTime.now();
        sequence.setCreateDatetime(now);
        sequence.setUpdateDatetime(now);
        sequence.setCurrentNumber(1L);
        sequence.setDefaultStepSize(stepSize == null || stepSize < 1 ? 1000 : stepSize);
        sequence.setVersion(1L);
        int count;
        try {
            count = sequenceDao.insert(sequence);
        } catch (RuntimeException e) {
            logger.warn("insert sequence key exception, key:{} stepSize:{}", key, stepSize);
            logger.warn("insert sequence key exception", e);
            throw SequenceSysMessage.SYS_SEQ_INSERT_KEY_FAIL.toSysException();
        }
        if (count > 0) {
            return sequence;
        } else {
            return null;
        }
    }

}
