package com.github.yanghyu.isid.common.sequence.model;

import lombok.Data;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 序列分段
 *
 * @author yanghongyu
 * @since 2020-08-21
 */
@Data
public class Segment {

    /**
     * 键
     */
    private String key;

    /**
     * 分段当前序列号
     */
    private AtomicLong currentNumber = new AtomicLong(0);

    /**
     * 分段最大序列号
     */
    private volatile long maxNumber;

    /**
     * 步长
     */
    private volatile int stepSize;

    /**
     * 修改时间
     */
    private Long updateTimestamp;

}
