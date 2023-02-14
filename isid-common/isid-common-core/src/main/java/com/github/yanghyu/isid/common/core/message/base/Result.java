package com.github.yanghyu.isid.common.core.message.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 结果
 * @param <T>   携带数据类型
 */
@ApiModel(value = "结果", description = "结果信息")
public class Result<T> implements Message<T> {

    /**
     * 结果编号
     */
    @ApiModelProperty("结果编号")
    private String code;

    /**
     * 结果描述
     */
    @ApiModelProperty("结果描述")
    private String desc;

    /**
     * 结果数据
     */
    @ApiModelProperty("结果数据")
    private T data;

    @Override
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Result(String code, String desc, T data) {
        this.code = code;
        this.desc = desc;
        this.data = data;
    }

    public Result(Message<?> message, T data) {
        this(message.getCode(), message.getDesc(), data);
    }

    public Result(Message<T> message) {
        this(message.getCode(), message.getDesc(), message.getData());
    }

    public Result(String code, String desc) {
        this.code = code;
        this.desc = desc;
        this.data = null;
    }

    public Result(T data) {
        this(Message.SUCCESS, data);
    }

    public Result() {
        this(Message.SUCCESS, null);
    }

    @Override
    public String toString() {
        return "Result{" +
                "code='" + code + '\'' +
                ", desc='" + desc + '\'' +
                ", data=" + data +
                '}';
    }

}
