package com.github.yanghyu.isid.common.core.generator;


import com.github.yanghyu.isid.common.core.message.base.Result;

/**
 * ID 生成器
 *
 * @author yanghongyu
 * @since 2020-06-18
 */
public interface IdGenerator {

    /**
     * 生成编号
     *
     * @param key   键名
     * @return      编号
     */
    Result<Long> generateNumber(String key);

    /**
     * 生成ID
     *
     * @param key   键名
     * @return      ID
     */
    Result<String> generateId(String key);

}
