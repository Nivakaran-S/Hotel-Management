package com.nivakaran.bookingservice.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI bookingServiceAPI() {
        return new OpenAPI()
                .info(new Info().title("Booking Service API")
                        .description("This is the REST API for Booking Service - Manages room and table bookings")
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0")))
                .externalDocs(new ExternalDocumentation()
                        .description("Booking Service Documentation")
                        .url("https://booking-service-docs.com"));
    }
}