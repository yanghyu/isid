package com.github.yanghyu.isid.web.autoconfigure.tomcat;

import com.github.yanghyu.isid.web.tomcat.config.WebTomcatConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@ConditionalOnProperty("server.additional-ports")
public class WebTomcatConfig extends WebTomcatConfigurer {
}
