package com.jagh.springcloud.app.gateway.filters.factory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class SampleCookieGatewayFilterFactory extends AbstractGatewayFilterFactory<SampleCookieGatewayFilterFactory.ConfigurationCookie> {

    private final Logger logger = LoggerFactory.getLogger(SampleCookieGatewayFilterFactory.class);

    public SampleCookieGatewayFilterFactory() {
        super(ConfigurationCookie.class);
    }

    @Override
    public GatewayFilter apply(ConfigurationCookie configurationCookie) {
        return new OrderedGatewayFilter((exchange, chain) -> {
            logger.info("Ejecutando pre gateway el filtro factory: " + configurationCookie.getMessage());
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                Optional.ofNullable(configurationCookie.getCookieValue())
                    .ifPresent(cookieValue -> {
                        exchange.getResponse().addCookie(ResponseCookie.from(configurationCookie.getCookieName(), cookieValue).build());
                    });
                logger.info("Ejecutando post gateway el filtro factory: " + configurationCookie.getMessage());
            }));
        }, 100);
    }
    
    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("message", "cookieName", "cookieValue");
    }


    
    @Override
    public String name() {
        return "EjemploCookie";
    }



    public static class ConfigurationCookie {
        private String cookieName;
        private String cookieValue;
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getCookieName() {
            return cookieName;
        }

        public void setCookieName(String cookieName) {
            this.cookieName = cookieName;
        }

        public String getCookieValue() {
            return cookieValue;
        }

        public void setCookieValue(String cookieValue) {
            this.cookieValue = cookieValue;
        }
    }

}
