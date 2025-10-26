package com.nivakaran.guestservice.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI guestServiceAPI() {
        return new OpenAPI()
                .info(new Info().title("Guest Service API")
                        .description("This is the REST API for Guest Service - Manages guest profiles and loyalty programs")
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0")))
                .externalDocs(new ExternalDocumentation()
                        .description("Guest Service Documentation")
                        .url("https://guest-service-docs.com"));
    }
}