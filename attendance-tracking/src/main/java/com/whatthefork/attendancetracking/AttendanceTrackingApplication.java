package com.whatthefork.attendancetracking;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(servers = {@Server(url = "/attendance-tracking", description = "Attendance Server URL")})
public class AttendanceTrackingApplication {

    public static void main(String[] args) {
        SpringApplication.run(AttendanceTrackingApplication.class, args);
    }

}
