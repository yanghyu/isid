package com.github.yanghyu.isid.common.sequence.message;


import com.github.yanghyu.isid.common.core.message.base.SysMessage;

public enum SequenceSysMessage implements SysMessage<Object> {

    SYS_SEQ_INSERT_KEY_FAIL("SYS_SEQ_INSERT_KEY_FAIL", "新增key失败"),
    SYS_SEQ_TOO_MANY_LOOP("SYS_SEQ_TOO_MANY_LOOP", "过多循环"),
    SYS_SEQ_LOOP_INTERRUPTED("SYS_SEQ_LOOP_INTERRUPTED", "循环中断"),
    ;

    /**
     * 消息异常编号
     */
    private final String code;

    /**
     * 消息异常描述
     */
    private final String desc;

    SequenceSysMessage(String code, String desc) {
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
