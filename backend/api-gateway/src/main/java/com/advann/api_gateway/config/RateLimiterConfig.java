package com.advann.api_gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import org.springframework.web.server.ServerWebExchange;

@Configuration
public class RateLimiterConfig {

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> Mono.just(getClientIP(exchange));
    }

    private String getClientIP(ServerWebExchange exchange) {

        String ip = exchange.getRequest()
                .getHeaders()
                .getFirst("X-Forwarded-For");

        if (ip == null) {
            ip = exchange.getRequest()
                    .getRemoteAddress()
                    .getAddress()
                    .getHostAddress();
        }

        return ip;
    }
}