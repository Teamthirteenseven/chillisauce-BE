package com.example.chillisauce.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {

    SecurityScheme securityScheme = new SecurityScheme()
            .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
            .in(SecurityScheme.In.HEADER).name("Authorization");
    SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

    @Bean
    public OpenAPI chillisauceOpenApi(){
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                .addSecurityItem(securityRequirement)
                .security(Collections.singletonList(securityRequirement))
                .info(new io.swagger.v3.oas.models.info.Info()
                .title("chillisauce API 명세서")
                .description("chillisauce API 명세서입니다.")
                .version("v0.0.1"));
    }
}
