package com.example.chillisauce.healthcheck;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
    @GetMapping("/server/healthcheck")
    public String healthCheck() {
        return "8080 health check OK";
    }
}
