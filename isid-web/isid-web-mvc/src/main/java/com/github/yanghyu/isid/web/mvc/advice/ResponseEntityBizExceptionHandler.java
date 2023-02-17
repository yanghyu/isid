package com.github.yanghyu.isid.web.mvc.advice;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import com.github.yanghyu.isid.common.core.http.OnetimeHttpHeaders;
import com.github.yanghyu.isid.common.core.message.base.BizMessage;
import com.github.yanghyu.isid.common.core.message.base.Result;
import com.github.yanghyu.isid.common.core.message.exception.BizException;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice(annotations = {Controller.class})
public class ResponseEntityBizExceptionHandler extends ResponseEntitySysExceptionHandler {

    @ExceptionHandler({
            BizException.class,
            HystrixBadRequestException.class,
            IllegalArgumentException.class
    })
    public final ResponseEntity<Object> handleBizException(Exception ex, WebRequest request) throws Exception {
        HttpHeaders headers = new HttpHeaders();

        if (ex instanceof BizException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return handleBusinessException((BizException) ex, headers, status, request);
        }
        else if (ex instanceof HystrixBadRequestException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return handleHystrixBadRequest((HystrixBadRequestException) ex, headers, status, request);
        }
        else if (ex instanceof IllegalArgumentException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return handleIllegalArgument((IllegalArgumentException) ex, headers, status, request);
        }
        else {
            // Unknown exception, typically a wrapper with a common MVC exception as cause
            // (since @ExceptionHandler type declarations also match first-level causes):
            // We only deal with top-level MVC exceptions here, so let's rethrow the given
            // exception for further processing through the HandlerExceptionResolver chain.
            throw ex;
        }
    }

    private ResponseEntity<Object> handleBusinessException(BizException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleBizExceptionInternal(ex, ex.toResult(), headers, status, request);
    }

    private ResponseEntity<Object> handleHystrixBadRequest(HystrixBadRequestException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String message = ex.getMessage();
        Result<Object> result = null;
        // 测试异常信息是否符合JSON格式
        try {
            result = JSON.parseObject(message, new TypeReference<Result<Object>>(){}.getType());
        } catch (RuntimeException e) {
            logger.warn("JSON.parseObject failed", e);
        }
        return handleBizExceptionInternal(ex, result, headers, status, request);
    }

    private ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleBizExceptionInternal(ex, null, headers, status, request);
    }

    /**
     * 默认定义的异常全按业务异常处理
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleBizExceptionInternal(ex, body, headers, status, request);
    }

    /**
     * A single place to customize the response body of all BizException types.
     *
     * @param ex the exception
     * @param body the body for the response
     * @param headers the headers for the response
     * @param status the response status
     * @param request the current request
     */
    private ResponseEntity<Object> handleBizExceptionInternal(
            Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        // 拦截5xx错误码改为400
        if (status.is5xxServerError()) {
            status = HttpStatus.BAD_REQUEST;
        }
        // 拦截body为空场景，返回固定格式
        if (body == null) {
            body = new Result<>(BizMessage.BIZ_FAIL.getCode(), BizMessage.BIZ_FAIL.getDesc() + "[(" + ex.getMessage() + ")]");
        }
        // 设置结果异常类型
        headers.set(OnetimeHttpHeaders.RESULT_EXCEPTION_TYPE, OnetimeHttpHeaders.ResultExceptionType.BIZ_EXCEPTION);
        // 打印异常日志
        logger.warn(body, ex);
        return new ResponseEntity<>(body, headers, status);
    }

}
