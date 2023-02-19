package com.github.yanghyu.isid.common.sequence.message;


import com.github.yanghyu.isid.common.core.message.base.BizMessage;

public enum SequenceBizMessage implements BizMessage<Object> {

    BIZ_SEQ_EMPTY_PARAMETER("BIZ_SEQ_EMPTY_PARAMETER", "参数为空")
    ;

    /**
     * 消息异常编号
     */
    private final String code;

    /**
     * 消息异常描述
     */
    private final String desc;

    SequenceBizMessage(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

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
        return null;
    }

}
