package io.github.hoo47.musinsa_assignment.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.extern.slf4j.Slf4j;

/**
 * Cache configuration class
 * Enhances performance using Redis cache.
 * Not applied in test environment
 */
@Slf4j
@Configuration
@Profile("!test")
@EnableCaching
public class CacheConfig implements CachingConfigurer {

    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;
    
    @Value("${spring.cache.redis.time-to-live:60}")
    private long timeToLive;
    
    @Value("${spring.data.redis.timeout:1000}")
    private long connectionTimeout;
    
    @Value("${spring.data.redis.client-name:musinsa-cache}")
    private String clientName;
    
    /**
     * Sets up Redis connection factory.
     * Detects connection failures quickly with explicit timeout settings.
     *
     * @return Redis connection factory
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
        
        // Explicit timeout settings and client name configuration
        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(connectionTimeout))
                .clientName(clientName)
                .build();
        
        return new LettuceConnectionFactory(config, clientConfig);
    }
    
    /**
     * Configures Redis cache manager.
     * Uses short TTL because data in commerce services may change frequently.
     * 
     * @param redisConnectionFactory Redis connection factory
     * @return Configured Redis cache manager
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // Basic Redis cache configuration
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(timeToLive))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())
                )
                .disableCachingNullValues();
        
        // Individual cache configuration for commerce service - Setting appropriate TTL
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withCacheConfiguration("priceSummaryCache", 
                        defaultConfig.entryTtl(Duration.ofSeconds(30))) // 30 seconds
                .withCacheConfiguration("categoryPricingCache", 
                        defaultConfig.entryTtl(Duration.ofSeconds(20))) // 20 seconds
                .withCacheConfiguration("brandLowestPriceCache", 
                        defaultConfig.entryTtl(Duration.ofSeconds(60))) // 1 minute
                .withCacheConfiguration("priceInfoCache", 
                        defaultConfig.entryTtl(Duration.ofSeconds(120))) // 2 minutes - Newly added cache
                .build();
    }
    
    /**
     * Configures Redis template.
     * 
     * @param redisConnectionFactory Redis connection factory
     * @return Configured Redis template
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
    
    /**
     * Configures cache error handler.
     * Executes the original method when errors occur such as Redis connection failures.
     */
    @Override
    public CacheErrorHandler errorHandler() {
        return new CustomCacheErrorHandler();
    }
    
    /**
     * Custom handler for cache error handling
     * Executes the original method when cache lookup/update fails.
     */
    public static class CustomCacheErrorHandler extends SimpleCacheErrorHandler {
        @Override
        public void handleCacheGetError(RuntimeException exception, org.springframework.cache.Cache cache, Object key) {
            super.handleCacheGetError(exception, cache, key);
        }

        @Override
        public void handleCachePutError(RuntimeException exception, org.springframework.cache.Cache cache, Object key, Object value) {
            super.handleCachePutError(exception, cache, key, value);
        }

        @Override
        public void handleCacheEvictError(RuntimeException exception, org.springframework.cache.Cache cache, Object key) {
            super.handleCacheEvictError(exception, cache, key);
        }

        @Override
        public void handleCacheClearError(RuntimeException exception, org.springframework.cache.Cache cache) {
            super.handleCacheClearError(exception, cache);
        }
    }
} 
