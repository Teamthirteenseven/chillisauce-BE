package com.example.chillisauce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ChillisauceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChillisauceApplication.class, args);
    }

}
