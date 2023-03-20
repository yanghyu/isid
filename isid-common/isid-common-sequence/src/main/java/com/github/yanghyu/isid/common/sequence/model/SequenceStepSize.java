package com.github.yanghyu.isid.common.sequence.model;

import lombok.Data;

/**
 * Key序列根据步长更新
 *
 * @author yanghongyu
 * @since 2020-06-18
 */
@Data
public class SequenceStepSize {

    /**
     * 主键编号
     */
    private String key;

    /**
     * 步长
     */
    private Integer stepSize;

}
