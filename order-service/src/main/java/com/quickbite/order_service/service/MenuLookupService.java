package com.quickbite.order_service.service;

import com.quickbite.order_service.client.MenuClient;
import com.quickbite.order_service.client.MenuItemDto;
import com.quickbite.order_service.exception.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

@Service
public class MenuLookupService {

    private static final Logger log = LoggerFactory.getLogger(MenuLookupService.class);

    private final MenuClient client;
    private final CircuitBreaker breaker;

    public MenuLookupService(MenuClient client) {
        this.client = client;

        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .permittedNumberOfCallsInHalfOpenState(3)
                .build();

        this.breaker = CircuitBreaker.of("restaurantService", config);
        this.breaker.getEventPublisher().onStateTransition(e ->
                log.warn("CIRCUIT BREAKER: {}", e.getStateTransition()));
    }

    public List<MenuItemDto> getItems(List<Long> ids) {
        Supplier<List<MenuItemDto>> call =
                CircuitBreaker.decorateSupplier(breaker, () -> client.getItems(ids));
        try {
            return call.get();
        } catch (Exception e) {
            log.warn("restaurant-service unavailable: {}", e.toString());
            throw new ServiceUnavailableException(
                    "Restaurant menu is temporarily unavailable, please try again shortly");
        }
    }

    public String currentState() {
        return breaker.getState().name();
    }
}