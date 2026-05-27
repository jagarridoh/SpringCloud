package com.jagh.springcloud.app.gateway.filters;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class SampleGlobalFilter implements GlobalFilter, Ordered {

    private final Logger logger = LoggerFactory.getLogger(SampleGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        logger.info("Ejecutando el filtro antes del request PREV");

        // (1) Crear un nuevo request con el Header:
        var mutatedRequest = exchange.getRequest()
            .mutate()
            .headers(h -> h.add("token", "mivalor"))
            .build();

        // (2) Crear un nuevo exchange con el request mutado:
        var mutatedExchange = exchange.mutate().request(mutatedRequest).build();

        return chain.filter(mutatedExchange)
            .then(Mono.fromRunnable(() -> {
                logger.info("Ejecutando el filtro POST response");
                String token = mutatedExchange.getRequest()
                    .getHeaders()
                    .getFirst("token");   //.get("token").get(0);
                if (token != null) {
                    logger.info("token: {}", token);
                }

            Optional.ofNullable(mutatedExchange.getRequest().getHeaders().getFirst("token"))
                .ifPresent(value -> {
                    logger.info("token response: {}", value);
                    mutatedExchange.getResponse().getHeaders().add("token", value);
                    mutatedExchange.getResponse().getHeaders().add("token2", value + "2");
                });

            mutatedExchange.getResponse().getCookies().add("color", ResponseCookie.from("color", "blue").build());
            //mutatedExchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
        }));


    }

    @Override
    public int getOrder() {
        return 100;
    }
}
