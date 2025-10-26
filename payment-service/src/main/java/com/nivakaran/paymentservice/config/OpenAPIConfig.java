package com.nivakaran.paymentservice.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI paymentServiceAPI() {
        return new OpenAPI()
                .info(new Info().title("Payment Service API")
                        .description("This is the REST API for Payment Service - Handles payment processing and transactions")
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0")))
                .externalDocs(new ExternalDocumentation()
                        .description("Payment Service Documentation")
                        .url("https://payment-service-docs.com"));
    }
}