package com.github.yanghyu.isid.web.autoconfigure.mvc;

import com.github.yanghyu.isid.common.core.message.converter.MessageMappingJackson2HttpMessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * WEB MVC 配置
 *
 * @author yanghyu
 * @since 2018-06-13 23:48
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ComponentScan("com.github.yanghyu.isid.web.mvc")
@ServletComponentScan("com.github.yanghyu.isid.web.mvc")
public class IsidWebMvcConfigurer implements WebMvcConfigurer {

    public IsidWebMvcConfigurer() {
        log.info("初始化IsidWebMvcConfigurer...");
    }

    /**
     * Jackson自定义配置
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0, new MessageMappingJackson2HttpMessageConverter());
    }

}
