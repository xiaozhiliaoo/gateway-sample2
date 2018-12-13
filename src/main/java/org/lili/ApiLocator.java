package org.lili;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * @author lili
 * @description ${DESCRIPTION}
 * @create 2018-12-13 13:20
 * @since
 **/
@EnableAutoConfiguration
@Configuration
@Log4j2
public class ApiLocator {

    @Autowired
    private RequestFilter requestFilter;

    private static final String SERVICE = "/path/**";
    private static final String URI = "http://127.0.0.1:8009";

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {

        /*
        route1 是get请求，get请求使用readBody会报错
        route2 是post请求，Content-Type是application/x-www-form-urlencoded，readbody为String.class
        route3 是post请求，Content-Type是application/json,readbody为Object.class
         */
        RouteLocatorBuilder.Builder routes = builder.routes();
        RouteLocatorBuilder.Builder serviceProvider = routes
                .route("route1",
                        r -> r
                                .method(HttpMethod.GET)
                                .and()
                                .path(SERVICE)
                                .filters(f -> {
                                    f.filter(requestFilter);
                                    return f;
                                })
                                .uri(URI))
                .route("route2",
                        r -> r
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                                .and()
                                .method(HttpMethod.POST)
                                .and()
                                .readBody(String.class, readBody -> {
                                    log.info("request method POST, Content-Type is application/x-www-form-urlencoded, body  is:{}", readBody);
                                    return true;
                                })
                                .and()
                                .path(SERVICE)
                                .filters(f -> {
                                    return f;
                                })
                                .uri(URI))
                .route("route3",
                        r -> r
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .and()
                                .method(HttpMethod.POST)
                                .and()
                                .readBody(Object.class, readBody -> {
                                    log.info("request method POST, Content-Type is application/json, body  is:{}", readBody);
                                    return true;
                                })
                                .and()
                                .path(SERVICE)
                                .filters(f -> {
                                    return f;
                                })
                                .uri(URI));
        RouteLocator routeLocator = serviceProvider.build();
        log.info("custom RouteLocator is loading ... {}", routeLocator);
        return routeLocator;
    }
}
