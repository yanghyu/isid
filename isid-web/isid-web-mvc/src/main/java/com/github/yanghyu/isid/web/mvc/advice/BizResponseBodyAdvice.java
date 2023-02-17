package com.github.yanghyu.isid.web.mvc.advice;


import com.github.yanghyu.isid.common.core.constant.StringConstantPool;
import com.github.yanghyu.isid.common.core.http.OnetimeHttpHeaders;
import com.github.yanghyu.isid.common.core.message.base.Message;
import com.github.yanghyu.isid.common.core.message.base.Result;
import com.github.yanghyu.isid.common.core.message.base.Unpackaged;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@Slf4j
@ControllerAdvice(annotations = {Controller.class})
public class BizResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(@NonNull MethodParameter returnType,
                            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return MappingJackson2HttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    public Object beforeBodyWrite(@Nullable Object body,
                                  @NonNull MethodParameter returnType,
                                  @NonNull MediaType selectedContentType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NonNull ServerHttpRequest request,
                                  @NonNull ServerHttpResponse response) {
        if (Unpackaged.class.isAssignableFrom(returnType.getParameterType())) {
            response.getHeaders().set(OnetimeHttpHeaders.PACKAGED_RESULT, StringConstantPool.ZERO);
            return body;
        }
        response.getHeaders().set(OnetimeHttpHeaders.PACKAGED_RESULT, StringConstantPool.ONE);
        if (Result.class.isAssignableFrom(returnType.getParameterType()) || body instanceof Result) {
            return body;
        }
        return new Result<>(Message.SUCCESS, body);
    }

}
