package com.whatthefork.gateway.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class FallbackControllerTest {

    private FallbackController fallbackController;

    @BeforeEach
    void setUp() {
        fallbackController = new FallbackController();
    }

    @Test
    @DisplayName("fallback 메소드가 에러 메시지를 반환한다")
    void fallback_ReturnsErrorMessage() {
        // when
        Mono<String> result = fallbackController.fallback();

        // then
        StepVerifier.create(result)
                .expectNext("Traffic overlad, please try againn later")
                .verifyComplete();
    }

    @Test
    @DisplayName("fallback 메소드가 Mono를 반환한다")
    void fallback_ReturnsMono() {
        // when
        Mono<String> result = fallbackController.fallback();

        // then
        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }
}