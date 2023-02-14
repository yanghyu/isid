package com.github.yanghyu.isid.common.core.message.base;

import com.github.yanghyu.isid.common.core.message.exception.SysException;

/**
 * 系统消息
 * @param <T>   携带数据类型
 */
public interface SysMessage<T> extends Message<T> {

    SysMessage<Object> SYS_FAIL = new SysMessage<Object>() {
        @Override
        public String getCode() {
            return "SYS_FAIL";
        }

        @Override
        public String getDesc() {
            return "系统繁忙";
        }

        @Override
        public Object getData() {
            return null;
        }
    };

    default SysException toSysException() {
        return new SysException(this);
    }

    default SysException toSysException(T data) {
        return new SysException(this, data);
    }

}
