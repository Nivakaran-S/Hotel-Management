package com.nivakaran.hotelservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
@Slf4j
public class CacheConfig implements CachingConfigurer {

    @Bean
    @ConditionalOnClass(name = "org.springframework.data.redis.connection.RedisConnectionFactory")
    @ConditionalOnProperty(name = "spring.redis.host")
    public RedisConnectionFactory redisConnectionFactory() {
        log.info("Configuring Redis connection factory");
        return new LettuceConnectionFactory();
    }

    @Bean
    @Primary
    @ConditionalOnBean(RedisConnectionFactory.class)
    @ConditionalOnClass(name = "org.springframework.data.redis.cache.RedisCacheManager")
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        log.info("Configuring Redis cache manager");

        try {
            // Default cache configuration
            RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(10))
                    .disableCachingNullValues()
                    .serializeKeysWith(RedisSerializationContext.SerializationPair
                            .fromSerializer(new StringRedisSerializer()))
                    .serializeValuesWith(RedisSerializationContext.SerializationPair
                            .fromSerializer(new GenericJackson2JsonRedisSerializer()));

            // Specific cache configurations with different TTL settings
            Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

            // Room cache - longer TTL since rooms don't change frequently
            cacheConfigurations.put("rooms", defaultConfig.entryTtl(Duration.ofHours(1)));

            // Available rooms - shorter TTL since availability changes frequently
            cacheConfigurations.put("availableRooms", defaultConfig.entryTtl(Duration.ofMinutes(5)));

            // Table cache - medium TTL
            cacheConfigurations.put("tables", defaultConfig.entryTtl(Duration.ofMinutes(30)));

            // Available tables - short TTL
            cacheConfigurations.put("availableTables", defaultConfig.entryTtl(Duration.ofMinutes(3)));

            // Hotel information - very long TTL
            cacheConfigurations.put("hotelInfo", defaultConfig.entryTtl(Duration.ofHours(6)));

            return RedisCacheManager.builder(connectionFactory)
                    .cacheDefaults(defaultConfig)
                    .withInitialCacheConfigurations(cacheConfigurations)
                    .transactionAware()
                    .build();

        } catch (Exception e) {
            log.error("Failed to configure Redis cache manager: {}", e.getMessage(), e);
            throw new RuntimeException("Redis cache configuration failed", e);
        }
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(RedisConnectionFactory.class)
    public ConcurrentMapCacheManager inMemoryCacheManager() {
        log.info("Redis not available, configuring in-memory cache manager");
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setCacheNames(java.util.List.of(
                "rooms", "availableRooms", "tables", "availableTables", "hotelInfo"
        ));
        cacheManager.setAllowNullValues(false);
        return cacheManager;
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new CustomCacheErrorHandler();
    }

    @Slf4j
    private static class CustomCacheErrorHandler extends SimpleCacheErrorHandler {
        @Override
        public void handleCacheGetError(RuntimeException exception, org.springframework.cache.Cache cache, Object key) {
            log.error("Cache GET error for cache: {} with key: {}", cache.getName(), key, exception);
            // Don't throw exception, just log it and continue without cache
        }

        @Override
        public void handleCachePutError(RuntimeException exception, org.springframework.cache.Cache cache, Object key, Object value) {
            log.error("Cache PUT error for cache: {} with key: {}", cache.getName(), key, exception);
            // Don't throw exception, just log it and continue without cache
        }

        @Override
        public void handleCacheEvictError(RuntimeException exception, org.springframework.cache.Cache cache, Object key) {
            log.error("Cache EVICT error for cache: {} with key: {}", cache.getName(), key, exception);
            // Don't throw exception, just log it and continue without cache
        }

        @Override
        public void handleCacheClearError(RuntimeException exception, org.springframework.cache.Cache cache) {
            log.error("Cache CLEAR error for cache: {}", cache.getName(), exception);
            // Don't throw exception, just log it and continue without cache
        }
    }
}