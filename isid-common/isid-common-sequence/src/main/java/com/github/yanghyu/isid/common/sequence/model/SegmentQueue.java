package com.github.yanghyu.isid.common.sequence.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 分段队列
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SegmentQueue extends LinkedBlockingQueue<Segment> {

    /**
     * 加载中标志
     */
    private boolean loading = false;

    /**
     * 开始加载毫秒值
     */
    private long loadStartTimeMillis = 0;

    /**
     * Lock held by loading, loadStartTimeMillis, etc.
     */
    private final ReentrantReadWriteLock lockReadWriteLock = new ReentrantReadWriteLock();

    public Lock getLoadReadLock() {
        return lockReadWriteLock.readLock();
    }

    public Lock getLoadWriteLock() {
        return lockReadWriteLock.writeLock();
    }

}
