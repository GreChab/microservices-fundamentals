package com.epam.resourceservice.faulttolerance;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class Breaker {

    private Breaker() {
    }

    public static CircuitBreaker circuitBreaker() {
        CircuitBreaker circuitBreaker = CircuitBreaker.of("storage service", CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .minimumNumberOfCalls(3)
                .slidingWindowSize(3)
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .waitDurationInOpenState(Duration.ofSeconds(5))
                .permittedNumberOfCallsInHalfOpenState(1)
                .build());

        circuitBreaker.getEventPublisher()
                .onEvent(event -> log.info("----- Circuit breaker event:" + event));

        return circuitBreaker;
    }


}
