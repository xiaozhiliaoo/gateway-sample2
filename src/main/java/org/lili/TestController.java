package org.lili;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author lili
 * @description ${DESCRIPTION}
 * @create 2018-12-10 20:14
 * @since
 **/
@RestController
public class TestController {
    // curl -XPOST -d "name=lili&age=45" 'http://127.0.0.1:8090/info'
    @RequestMapping(path = "/info", method = {RequestMethod.POST})
    public String get(ServerWebExchange serverWebExchange) {
        ServerHttpRequest request = serverWebExchange.getRequest();
        String body = resolveBodyFromRequest(request);
        //spring-boot-starter-parent 2.1.0.RELEASE + Spring Cloud Greenwich.M3 body always null
        //but in spring-boot-starter-parent 2.0.6.RELEASE + Spring Cloud Finchley.SR2 body is correct
        // i don`t know why?
        System.out.println("body is:" + body);
        return "ok";
    }

    private String resolveBodyFromRequest(ServerHttpRequest serverHttpRequest) {
        Flux<DataBuffer> body = serverHttpRequest.getBody();
        AtomicReference<String> bodyRef = new AtomicReference<>();
        body.subscribe(buffer -> {
            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
            DataBufferUtils.release(buffer);
            bodyRef.set(charBuffer.toString());
        });
        return bodyRef.get();
    }
}
