package com.github.yanghyu.isid.common.core.message.base;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "单字段Body", description = "单字段Body")
public class SingleFieldBody<T> {

    private T value;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "SingleFieldBody{" +
                "value=" + value +
                '}';
    }

}
