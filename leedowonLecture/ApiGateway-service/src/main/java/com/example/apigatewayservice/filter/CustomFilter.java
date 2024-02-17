package com.example.apigatewayservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class CustomFilter extends AbstractGatewayFilterFactory<CustomFilter.Config> {

    public CustomFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {  // Custom Pre Filter 처리전
            ServerHttpRequest request = exchange.getRequest();//ServerHttpRequest는 비동기 방식
            ServerHttpResponse response = exchange.getResponse();//ServerHttpResponse는 비동기 방식

            log.info("Custom PRE filter: request id -> {}", request.getId());

            return chain.filter(exchange).then(Mono.fromRunnable(() -> { // Custom Post Filter 처리후
                log.info("Custom POST filter: response code -> {}", response.getStatusCode());
            }));
        };
    }

    public static class Config {
        // Put the configuration properties
    }
}
