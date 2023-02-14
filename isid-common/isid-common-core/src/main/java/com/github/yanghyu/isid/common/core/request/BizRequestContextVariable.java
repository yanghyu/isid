package com.github.yanghyu.isid.common.core.request;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableDefault;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求上下文变量
 */
public class BizRequestContextVariable extends HystrixRequestVariableDefault<BizRequestContextVariable> {

    private static final BizRequestContextVariable VARIABLE = new BizRequestContextVariable();

    /**
     * HTTP头集合
     */
    private final Map<String, String> headers = new HashMap<>();

    /**
     * 设置HTTP头
     *
     * @param headerName    HTTP头名
     * @param headerValue   HTTP头值
     */
    public void setHeader(String headerName, String headerValue) {
        this.headers.put(headerName, headerValue);
    }

    /**
     * 批量设置HTTP头
     *
     * @param headers   HTTP头集合
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
    }

    /**
     * 获取HTTP头
     *
     * @param headerName    HTTP头名
     * @return  HTTP头值
     */
    public String getHeader(String headerName) {
        return this.headers.get(headerName);
    }

    /**
     * 批量获取HTTP头
     *
     * @return   HTTP头集合
     */
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    /**
     * 私有构造方法
     */
    private BizRequestContextVariable() { }

    /**
     * 初始化方法
     *
     * @return 对象
     */
    @Override
    public BizRequestContextVariable initialValue() {
        return new BizRequestContextVariable();
    }

    /**
     * 获取当前线程对象
     *
     * @return Variable
     */
    public static BizRequestContextVariable getVariable() {
        return VARIABLE.get();
    }

    /**
     * 移除当前线程对象
     */
    public static void removeVariable() {
        VARIABLE.remove();
    }

}
