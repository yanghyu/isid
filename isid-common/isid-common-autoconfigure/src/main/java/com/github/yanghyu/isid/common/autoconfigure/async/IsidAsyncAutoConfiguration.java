package com.github.yanghyu.isid.common.autoconfigure.async;

import com.github.yanghyu.isid.common.async.config.IsidAsyncConfigurer;
import com.github.yanghyu.isid.common.async.executor.AsyncExecutor;
import com.github.yanghyu.isid.common.async.handler.AsyncHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@ConditionalOnClass({IsidAsyncConfigurer.class})
@Import({IsidAsyncConfigurer.class})
public class IsidAsyncAutoConfiguration {

    @Bean
    public AsyncExecutor asyncExecutor() {
        return new AsyncExecutor();
    }

    @Bean
    public AsyncHandler asyncHandler(AsyncExecutor asyncExecutor) {
        return new AsyncHandler(asyncExecutor);
    }

}
