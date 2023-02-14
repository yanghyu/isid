package com.github.yanghyu.isid.common.core.message.exception;

import com.github.yanghyu.isid.common.core.message.base.Message;
import com.github.yanghyu.isid.common.core.message.base.SysMessage;

/**
 * 系统异常
 */
public class SysException extends RuntimeException implements SysMessage<Object> {

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

    public SysException(String code, String desc, Object data) {
        super(Message.toJsonString(code, desc, data));
        this.code = code;
        this.desc = desc;
        this.data = data;
    }

    public SysException(Message<?> message, Object data) {
        this(message.getCode(), message.getDesc(), data);
    }

    public SysException(Message<?> message) {
        this(message, message.getData());
    }

    public SysException(String code, String desc) {
        this(code, desc, null);
    }

    public SysException(String desc) {
        this(SysMessage.SYS_FAIL.getCode(), desc, null);
    }

    public SysException(Object data) {
        this(SysMessage.SYS_FAIL, data);
    }

    public SysException() {
        this(SysMessage.SYS_FAIL);
    }

    @Override
    public String toString() {
        return "SysException{" +
                "code='" + code + '\'' +
                ", desc='" + desc + '\'' +
                ", data=" + data +
                '}';
    }

}
