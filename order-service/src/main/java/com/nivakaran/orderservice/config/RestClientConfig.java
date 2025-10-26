package com.nivakaran.orderservice.config;

import com.nivakaran.orderservice.client.RestaurantClient;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.service.invoker.HttpServiceProxyFactory.Builder;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    @Value("${restaurant.service.url}")
    private String restaurantServiceUrl;

    private final ObservationRegistry observationRegistry;

    @Bean
    public RestaurantClient restaurantClient() {
        RestTemplate restTemplate = new RestTemplate(getClientRequestFactory());

        restTemplate.getInterceptors().add((request, body, execution) -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
                request.getHeaders().setBearerAuth(jwt.getTokenValue());
            }
            return execution.execute(request, body);
        });

        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder().build();
        return factory.createClient(RestaurantClient.class);
    }

    private ClientHttpRequestFactory getClientRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // Set timeouts in milliseconds
        factory.setConnectTimeout((int) Duration.ofSeconds(5).toMillis());
        factory.setReadTimeout((int) Duration.ofSeconds(5).toMillis());
        return factory;
    }
}
