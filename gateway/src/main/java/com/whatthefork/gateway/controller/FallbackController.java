package com.whatthefork.gateway.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

    @RequestMapping("/fallback/user")
    public Mono<String> fallback() {
        return Mono.just("Traffic overlad, please try againn later");
    }

}
