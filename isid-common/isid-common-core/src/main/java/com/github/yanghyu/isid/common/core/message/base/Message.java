package com.github.yanghyu.isid.common.core.message.base;

import com.alibaba.fastjson.JSON;

import java.util.LinkedHashMap;

/**
 * 消息
 * @param <T>   携带数据类型
 */
public interface Message<T> {

    Message<Object> SUCCESS = new Message<Object>() {
        @Override
        public String getCode() {
            return "SUCCESS";
        }

        @Override
        public String getDesc() {
            return "成功";
        }

        @Override
        public Object getData() {
            return null;
        }
    };

    /**
     * 消息编码
     * @return  编码
     */
    String getCode();

    /**
     * 消息描述
     * @return  描述
     */
    String getDesc();

    /**
     * 消息数据
     * @return  数据
     */
    T getData();

    /**
     * 转成JSON格式消息
     * @return  JSON格式消息
     */
    default String toJsonString() {
        return toJsonString(getCode(), getDesc(), getData());
    }

    static String toJsonString(String code, String desc, Object data) {
        LinkedHashMap<String, Object> message = new LinkedHashMap<>(3);
        message.put("code", code);
        message.put("desc", desc);
        message.put("data", data);
        return JSON.toJSONString(message);
    }

    /**
     * 转为结果
     * @param data  携带数据
     * @return 结果
     */
    default Result<T> toResult(T data) {
        return toResult(getCode(), getDesc(), data);
    }

    /**
     * 转为结果
     * @return 结果
     */
    default Result<T> toResult() {
        return toResult(getCode(), getDesc(), getData());
    }

    static <T> Result<T> toResult(String code, String desc, T data) {
        return new Result<>(code, desc, data);
    }

}
