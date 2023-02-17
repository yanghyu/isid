package com.github.yanghyu.isid.common.async.executor;

import com.github.yanghyu.isid.common.async.bean.Task;
import com.github.yanghyu.isid.common.async.bean.VoidTask;
import com.github.yanghyu.isid.common.core.message.exception.BizException;
import com.github.yanghyu.isid.common.core.message.exception.SysException;
import com.github.yanghyu.isid.common.core.request.BizRequestContextVariableAssist;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.Map;
import java.util.concurrent.Future;

@Slf4j
public class AsyncExecutor {

    @Async("AsyncExecutorCallerRunsPolicy")
    public void execCallerRunsPolicy(VoidTask task, Map<String, String> headers) {
        exec(task, headers);
    }

    @Async("AsyncExecutorDiscardPolicy")
    public void execDiscardPolicy(VoidTask task, Map<String, String> headers) {
        exec(task, headers);
    }

    @Async("AsyncExecutorAbortPolicy")
    public void execAbortPolicy(VoidTask task, Map<String, String> headers) {
        exec(task, headers);
    }

    @Async("AsyncExecutorCallerRunsPolicy")
    public <R> Future<R> execCallerRunsPolicy(Task<R> task, Map<String, String> headers) {
        return exec(task, headers);
    }

    @Async("AsyncExecutorDiscardPolicy")
    public <R> Future<R> execDiscardPolicy(Task<R> task, Map<String, String> headers) {
        return exec(task, headers);
    }

    @Async("AsyncExecutorAbortPolicy")
    public <R> Future<R> execAbortPolicy(Task<R> task, Map<String, String> headers) {
        return exec(task, headers);
    }

    private <R> Future<R> exec(Task<R> task, Map<String, String> headers) {
        try {
            BizRequestContextVariableAssist.init(headers);
            return AsyncResult.forValue(task.exec());
        } catch (BizException be) {
            log.warn(be.toJsonString(), be);
            return AsyncResult.forExecutionException(be);
        } catch (SysException se) {
            log.error(se.toJsonString(), se);
            return AsyncResult.forExecutionException(se);
        } catch (RuntimeException re) {
            log.error(task.getClass().getName(), re);
            return AsyncResult.forExecutionException(re);
        } finally {
            BizRequestContextVariableAssist.close();
        }
    }

}
