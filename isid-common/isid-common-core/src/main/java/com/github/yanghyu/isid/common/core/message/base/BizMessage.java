package com.github.yanghyu.isid.common.core.message.base;

import com.github.yanghyu.isid.common.core.message.exception.BizException;

/**
 * 业务消息
 * @param <T>   携带数据类型
 */
public interface BizMessage<T> extends Message<T> {

    BizMessage<Object> BIZ_FAIL = new BizMessage<Object>() {
        @Override
        public String getCode() {
            return "BIZ_FAIL";
        }

        @Override
        public String getDesc() {
            return "请求失败";
        }

        @Override
        public Object getData() {
            return null;
        }
    };

    default BizException toBizException() {
        return new BizException(this);
    }

    default BizException toBizException(T data) {
        return new BizException(this, data);
    }

}
