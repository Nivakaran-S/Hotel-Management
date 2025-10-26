package com.nivakaran.restaurantservice.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI restaurantServiceAPI() {
        return new OpenAPI()
                .info(new Info().title("Restaurant Service API")
                        .description("This is the REST API for Restaurant Service - Manages menu items and food orders")
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0")))
                .externalDocs(new ExternalDocumentation()
                        .description("Restaurant Service Documentation")
                        .url("https://restaurant-service-docs.com"));
    }
}