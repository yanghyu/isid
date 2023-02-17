package com.github.yanghyu.isid.web.mvc.advice;


import com.github.yanghyu.isid.common.core.http.OnetimeHttpHeaders;
import com.github.yanghyu.isid.common.core.message.base.Result;
import com.github.yanghyu.isid.common.core.message.base.SysMessage;
import com.github.yanghyu.isid.common.core.message.exception.SysException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

public class ResponseEntitySysExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            SysException.class,
            RuntimeException.class
    })
    @Nullable
    public final ResponseEntity<Object> handleSysException(Exception ex, WebRequest request) throws Exception {
        HttpHeaders headers = new HttpHeaders();

        if (ex instanceof SysException) {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            return handleSysException((SysException) ex, headers, status, request);
        }
        else if (ex instanceof RuntimeException) {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            return handleRuntimeException((RuntimeException) ex, headers, status, request);
        }
        else {
            // Unknown exception, typically a wrapper with a common MVC exception as cause
            // (since @ExceptionHandler type declarations also match first-level causes):
            // We only deal with top-level MVC exceptions here, so let's rethrow the given
            // exception for further processing through the HandlerExceptionResolver chain.
            throw ex;
        }
    }

    private ResponseEntity<Object> handleSysException(SysException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleSysExceptionInternal(ex, ex.toResult(), headers, status, request);
    }

    private ResponseEntity<Object> handleRuntimeException(RuntimeException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Result<Object> result = SysMessage.SYS_FAIL.toResult();
        return handleSysExceptionInternal(ex, result, headers, status, request);
    }

    /**
     * A single place to customize the response body of all SysException types.
     *
     * @param ex the exception
     * @param body the body for the response
     * @param headers the headers for the response
     * @param status the response status
     * @param request the current request
     */
    protected ResponseEntity<Object> handleSysExceptionInternal(
            Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        // 设置结果异常类型
        headers.set(OnetimeHttpHeaders.RESULT_EXCEPTION_TYPE, OnetimeHttpHeaders.ResultExceptionType.SYS_EXCEPTION);
        // 记录异常日志
        logger.error(body, ex);
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }
        return new ResponseEntity<>(body, headers, status);
    }

}
