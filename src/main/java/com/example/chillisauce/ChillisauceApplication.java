package com.example.chillisauce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Flexidesk backend source code
 * @version 1.0
 * @author 이민재, 임상규, 장혁진
 */
@SpringBootApplication
@EnableCaching
public class ChillisauceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChillisauceApplication.class, args);
    }

}
