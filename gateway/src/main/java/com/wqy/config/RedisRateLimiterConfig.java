package com.wqy.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class RedisRateLimiterConfig {

    @Bean
    KeyResolver userKeyResolver() {
        return new KeyResolver(){

            @Override
            public Mono<String> resolve(ServerWebExchange exchange) {
                return Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());

            }
        };
        //  return exchange -> Mono.just(exchange.getRequest().getQueryParams().getFirst("userName"));
    }
}