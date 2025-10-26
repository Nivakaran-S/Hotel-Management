package com.nivakaran.staffservice.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI staffServiceAPI() {
        return new OpenAPI()
                .info(new Info().title("Staff Service API")
                        .description("This is the REST API for Staff Service - Manages hotel staff and employees")
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0")))
                .externalDocs(new ExternalDocumentation()
                        .description("Staff Service Documentation")
                        .url("https://staff-service-docs.com"));
    }
}