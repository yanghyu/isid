package com.github.yanghyu.isid.common.sequence.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Key序列
 *
 * @author yanghongyu
 * @since 2020-06-18
 */
@Data
public class Sequence {

    /**
     * 主键编号
     */
    private String key;

    /**
     * 创建时间
     */
    private LocalDateTime createDatetime;

    /**
     * 修改时间
     */
    private LocalDateTime updateDatetime;

    /**
     * 最大序列号
     */
    private Long currentNumber;

    /**
     * 步长
     */
    private Integer defaultStepSize;

    /**
     * 版本号
     */
    private Long version;

}
