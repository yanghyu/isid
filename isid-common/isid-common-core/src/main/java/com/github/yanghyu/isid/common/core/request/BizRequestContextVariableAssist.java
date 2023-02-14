package com.github.yanghyu.isid.common.core.request;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


public class BizRequestContextVariableAssist {

    public static void init(Map<String, String> headers) {
        // 调用上下文
        if (!HystrixRequestContext.isCurrentThreadInitialized()) {
            HystrixRequestContext.initializeContext();
        }
        BizRequestContextVariable variable = BizRequestContextVariable.getVariable();
        variable.setHeaders(headers);
    }

    public static void init(HttpServletRequest httpServletRequest) {
        Enumeration<String> enumeration = httpServletRequest.getHeaderNames();
        Map<String, String> headers = new HashMap<>();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            headers.put(name, httpServletRequest.getHeader(name));
        }
        init(headers);
    }

    public static void close() {
        BizRequestContextVariable.removeVariable();
        if (HystrixRequestContext.isCurrentThreadInitialized()) {
            HystrixRequestContext.getContextForCurrentThread().close();
        }
    }

    public static Map<String, String> getHeaders() {
        // 调用上下文
        if (!HystrixRequestContext.isCurrentThreadInitialized()) {
            HystrixRequestContext.initializeContext();
        }

        BizRequestContextVariable variable = BizRequestContextVariable.getVariable();
        return variable.getHeaders();
    }


}
