package com.github.yanghyu.isid.app.service.online;

import com.github.yanghyu.isid.web.autoconfigure.tomcat.WebTomcatConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import({WebTomcatConfig.class})
@SpringBootApplication
public class ServiceOnlineApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceOnlineApplication.class, args);
    }

}
