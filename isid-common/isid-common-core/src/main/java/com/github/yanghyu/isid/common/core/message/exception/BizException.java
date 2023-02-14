package com.github.yanghyu.isid.common.core.message.exception;

import com.github.yanghyu.isid.common.core.message.base.BizMessage;
import com.github.yanghyu.isid.common.core.message.base.Message;
import com.netflix.hystrix.exception.HystrixBadRequestException;

/**
 * 业务异常
 */
public class BizException extends HystrixBadRequestException implements BizMessage<Object> {

    /**
     * 消息异常编号
     */
    private final String code;

    /**
     * 消息异常描述
     */
    private final String desc;

    /**
     * 消息异常数据
     */
    private final Object data;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    @Override
    public Object getData() {
        return data;
    }

    public BizException(String code, String desc, Object data) {
        super(Message.toJsonString(code, desc, data));
        this.code = code;
        this.desc = desc;
        this.data = data;
    }

    public BizException(Message<?> message, Object data) {
        this(message.getCode(), message.getDesc(), data);
    }

    public BizException(Message<?> message) {
        this(message, message.getData());
    }

    public BizException(String code, String desc) {
        this(code, desc, null);
    }

    public BizException(String desc) {
        this(BizMessage.BIZ_FAIL.getCode(), desc, null);
    }

    public BizException(Object data) {
        this(BizMessage.BIZ_FAIL, data);
    }

    public BizException() {
        this(BizMessage.BIZ_FAIL);
    }

    @Override
    public String toString() {
        return "BizException{" +
                "code='" + code + '\'' +
                ", desc='" + desc + '\'' +
                ", data=" + data +
                '}';
    }
}
