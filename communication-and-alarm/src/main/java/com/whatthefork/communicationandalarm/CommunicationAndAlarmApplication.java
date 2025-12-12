package com.whatthefork.communicationandalarm;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@OpenAPIDefinition(
        servers = {
                @Server(url = "/api/v1/communication-and-alarm", description = "Gateway Server URL")
        }
)
@EnableFeignClients(basePackages = "com.whatthefork.communicationandalarm.client")
// @EnableFeignClients(basePackages = "com.whatthefork.communicationandalarm")
public class CommunicationAndAlarmApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommunicationAndAlarmApplication.class, args);
    }

}
