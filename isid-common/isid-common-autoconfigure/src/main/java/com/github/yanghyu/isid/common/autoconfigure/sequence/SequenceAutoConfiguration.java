package com.github.yanghyu.isid.common.autoconfigure.sequence;


import com.github.yanghyu.isid.common.core.generator.IdGenerator;
import com.github.yanghyu.isid.common.sequence.generator.IdGeneratorImpl;
import com.github.yanghyu.isid.common.sequence.handler.SegmentHandler;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

/**
 * Leaf自动配置
 *
 * @author yanghongyu
 * @since 2020-07-29
 */
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@ConditionalOnBean(DataSource.class)
@ConditionalOnClass(IdGeneratorImpl.class)
public class SequenceAutoConfiguration {

    @Bean
    public SegmentHandler segmentHandler(DataSource dataSource){
        return new SegmentHandler(dataSource);
    }

    @Bean
    public IdGenerator idGenerator(SegmentHandler segmentHandler) {
        return new IdGeneratorImpl(segmentHandler);
    }

}
