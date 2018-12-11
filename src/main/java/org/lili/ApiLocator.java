package org.lili;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lili
 * @description ${DESCRIPTION}
 * @create 2018-12-11 11:49
 * @since
 **/
@EnableAutoConfiguration
@Configuration
@Log4j2
public class ApiLocator {

    @Autowired
    private RequestFilter requestFilter;

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {

        RouteLocatorBuilder.Builder serviceProvider = builder.
                routes().route("gateway-sample",
                    r -> r.readBody(Object.class, requestBody -> {
                        log.info("requestBody is {}", requestBody);
                        // 这里不对body做判断处理
                        return true;
                }).and().path("/service").
                        filters(f -> {
                            f.filter(requestFilter);
                            return f;
                        })
                        .uri("http://127.0.0.1:8009"));
        RouteLocator routeLocator = serviceProvider.build();
        log.info("custom RouteLocator is loading ... {}", routeLocator);
        return routeLocator;
    }
}
