package com.github.yanghyu.isid.common.async.handler;

import com.github.yanghyu.isid.common.async.bean.Task;
import com.github.yanghyu.isid.common.async.bean.VoidTask;
import com.github.yanghyu.isid.common.async.executor.AsyncExecutor;
import com.github.yanghyu.isid.common.core.request.BizRequestContextVariableAssist;

import java.util.Map;
import java.util.concurrent.Future;

public class AsyncHandler {

    private final AsyncExecutor asyncExecutor;

    public AsyncHandler(AsyncExecutor asyncExecutor) {
        this.asyncExecutor = asyncExecutor;
    }

    public void execCallerRunsPolicy(VoidTask task) {
        Map<String, String> headers = BizRequestContextVariableAssist.getHeaders();
        asyncExecutor.execCallerRunsPolicy(task, headers);
    }

    public void execDiscardPolicy(VoidTask task) {
        Map<String, String> headers = BizRequestContextVariableAssist.getHeaders();
        asyncExecutor.execDiscardPolicy(task, headers);
    }

    public void execAbortPolicy(VoidTask task) {
        Map<String, String> headers = BizRequestContextVariableAssist.getHeaders();
        asyncExecutor.execAbortPolicy(task, headers);
    }

    public <R> Future<R> execCallerRunsPolicy(Task<R> task) {
        Map<String, String> headers = BizRequestContextVariableAssist.getHeaders();
        return asyncExecutor.execCallerRunsPolicy(task, headers);
    }

    public <R> Future<R> execDiscardPolicy(Task<R> task) {
        Map<String, String> headers = BizRequestContextVariableAssist.getHeaders();
        return asyncExecutor.execDiscardPolicy(task, headers);
    }

    public <R> Future<R> execAbortPolicy(Task<R> task) {
        Map<String, String> headers = BizRequestContextVariableAssist.getHeaders();
        return asyncExecutor.execAbortPolicy(task, headers);
    }

}
