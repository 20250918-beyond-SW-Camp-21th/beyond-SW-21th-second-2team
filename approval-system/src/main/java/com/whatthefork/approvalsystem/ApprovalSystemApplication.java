package com.whatthefork.approvalsystem;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@OpenAPIDefinition(
        servers = {
                @Server(url = "/api/v1/approval-system", description = "Gateway Server URL")
        }
)
@EnableFeignClients
public class ApprovalSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApprovalSystemApplication.class, args);
    }

}
