package com.nivakaran.hotelservice.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI hotelServiceAPI() {
        return new OpenAPI()
                .info(new Info().title("Hotel Service API")
                        .description("This is the REST API for Hotel Service - Manages rooms and restaurant tables")
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0")))
                .externalDocs(new ExternalDocumentation()
                        .description("Hotel Service Documentation")
                        .url("https://hotel-service-docs.com"));
    }
}