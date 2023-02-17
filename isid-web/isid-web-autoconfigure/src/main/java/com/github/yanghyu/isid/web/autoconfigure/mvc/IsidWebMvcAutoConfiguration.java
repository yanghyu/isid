package com.github.yanghyu.isid.web.autoconfigure.mvc;



import com.github.yanghyu.isid.web.mvc.advice.BizRequestBodyAdvice;
import com.github.yanghyu.isid.web.mvc.advice.BizResponseBodyAdvice;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;

/**
 * WEB MVC 自动配置
 *
 * @author yanghongyu
 * @since 2021-12-11
 */
@ConditionalOnClass({BizRequestBodyAdvice.class, BizResponseBodyAdvice.class})
@Import({IsidWebMvcConfigurer.class})
public class IsidWebMvcAutoConfiguration {

}
