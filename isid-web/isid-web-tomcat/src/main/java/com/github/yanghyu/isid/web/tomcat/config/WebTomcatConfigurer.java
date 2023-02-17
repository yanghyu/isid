package com.github.yanghyu.isid.web.tomcat.config;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

public class WebTomcatConfigurer {

    @Value("#{'${server.additional-ports}'.split(',')}")
    private List<String> additionalPorts;

    @Bean
    public TomcatServletWebServerFactory tomcatServletWebServerFactory() {
        TomcatServletWebServerFactory tomcatServletWebServerFactory = new TomcatServletWebServerFactory();
        Connector[] additionalConnectors = this.additionalConnector();
        if (additionalConnectors.length > 0) {
            tomcatServletWebServerFactory.addAdditionalTomcatConnectors(additionalConnectors);
        }
        return tomcatServletWebServerFactory;
    }

    private Connector[] additionalConnector() {
        List<Connector> result = new ArrayList<>();
        for (String port : additionalPorts) {
            Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
            connector.setScheme("http");
            connector.setPort(Integer.parseInt(port));
            result.add(connector);
        }
        return result.toArray(new Connector[] {});
    }

}
