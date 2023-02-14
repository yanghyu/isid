package com.github.yanghyu.isid.common.core.message.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * JSON格式转换
 *
 * @author YANGHONGYU685
 * @since 2019-06-29
 *
 */
public class MessageMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {

    private static final String PATTERN_DATE_TIME = "yyyy-MM-dd HH:mm:ss";

    private static final String PATTERN_DATE = "yyyy-MM-dd";

    private static final String PATTERN_TIME = "HH:mm:ss";


    public MessageMappingJackson2HttpMessageConverter() {
        ObjectMapper objectMapper = super.getObjectMapper();

        // 数字Long类型转为字符串输出
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);

        // 自定义日期格式化
        simpleModule.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(PATTERN_DATE_TIME)));
        simpleModule.addSerializer(LocalDate.class,
                new LocalDateSerializer(DateTimeFormatter.ofPattern(PATTERN_DATE)));
        simpleModule.addSerializer(LocalTime.class,
                new LocalTimeSerializer(DateTimeFormatter.ofPattern(PATTERN_TIME)));
        simpleModule.addSerializer(Date.class,
                new DateSerializer(false, new SimpleDateFormat(PATTERN_DATE_TIME)));

        simpleModule.addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(PATTERN_DATE_TIME)));
        simpleModule.addDeserializer(LocalDate.class,
                new LocalDateDeserializer(DateTimeFormatter.ofPattern(PATTERN_DATE)));
        simpleModule.addDeserializer(LocalTime.class,
                new LocalTimeDeserializer(DateTimeFormatter.ofPattern(PATTERN_TIME)));

        objectMapper.registerModule(simpleModule);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        super.setObjectMapper(objectMapper);
    }

}
