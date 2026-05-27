package com.jagh.springcloud.msvc.items;

import java.time.Duration;

import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;

@Configuration
public class AppConfig {

    private static final CircuitBreakerConfig CB_CONFIG = CircuitBreakerConfig.custom()
        .slidingWindowSize(10)
        .failureRateThreshold(50)
        .waitDurationInOpenState(Duration.ofSeconds(10L))
        .permittedNumberOfCallsInHalfOpenState(5)
        .build();
    
    private static final TimeLimiterConfig TL_CONFIG = TimeLimiterConfig.custom()
        .timeoutDuration(Duration.ofSeconds(6L))
        .build();

    // Este bean reemplaza al autoconfigurado por Spring → máxima prioridad    
    @Bean
    TimeLimiterRegistry timeLimiterRegistry() {
        return TimeLimiterRegistry.of(TL_CONFIG);
    }

    // Este bean reemplaza al autoconfigurado por Spring → máxima prioridad    
    @Bean
    CircuitBreakerRegistry circuitBreakerRegistry() {
        return CircuitBreakerRegistry.of(CB_CONFIG);
    }

    // El Customizer sigue siendo necesario para enlazar los registries con la factory  
    @Bean
    Customizer<Resilience4JCircuitBreakerFactory> customizerCircuitBreaker(
        CircuitBreakerRegistry cbRegistry, TimeLimiterRegistry tlRegistry) {
        return factory -> {
            factory.configureCircuitBreakerRegistry(cbRegistry);
            factory.configureDefault(id ->
                new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(cbRegistry.getDefaultConfig())
                .timeLimiterConfig(tlRegistry.getDefaultConfig())
                .build()
            );
        };
    }
}
/*        @Bean
        Customizer<Resilience4JCircuitBreakerFactory> customizerCircuitBreaker () {
            return (factory) -> factory.configureDefault(id -> {
                return new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig
                    .custom()
                    .slidingWindowSize(10)
                    .failureRateThreshold(50.0f)
                    .waitDurationInOpenState(java.time.Duration.ofSeconds(10))
                    .permittedNumberOfCallsInHalfOpenState(5)
                    .build())
                    .timeLimiterConfig(TimeLimiterConfig
                        .custom()
                        .timeoutDuration(Duration.ofSeconds(10L))
                        .build())
                .build();
            });
} */
