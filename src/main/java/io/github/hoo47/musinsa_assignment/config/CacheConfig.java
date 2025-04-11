package io.github.hoo47.musinsa_assignment.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
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
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.github.hoo47.musinsa_assignment.application.product.dto.response.CategoryProductSummaryResponse;
import io.github.hoo47.musinsa_assignment.domain.product.Product;
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
    
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
    
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
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory, ObjectMapper objectMapper) {
        Jackson2JsonRedisSerializer<CategoryProductSummaryResponse> categorySerializer = 
            new Jackson2JsonRedisSerializer<>(objectMapper, CategoryProductSummaryResponse.class);
        Jackson2JsonRedisSerializer<Product> productSerializer = 
            new Jackson2JsonRedisSerializer<>(objectMapper, Product.class);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(timeToLive))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper))
                )
                .disableCachingNullValues();

        // 각 캐시별로 다른 직렬화 설정 적용
        RedisCacheConfiguration categoryConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(20))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(categorySerializer)
                );

        RedisCacheConfiguration productConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(120))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(productSerializer)
                );

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withCacheConfiguration("categoryPricingCache", categoryConfig)
                .withCacheConfiguration("priceInfoCache", productConfig)
                .withCacheConfiguration("priceSummaryCache", 
                        defaultConfig.entryTtl(Duration.ofSeconds(30)))
                .withCacheConfiguration("brandLowestPriceCache", 
                        defaultConfig.entryTtl(Duration.ofSeconds(60)))
                .build();
    }
    
    /**
     * Configures Redis template.
     * 
     * @param redisConnectionFactory Redis connection factory
     * @return Configured Redis template
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory, ObjectMapper objectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        return template;
    }
    
    /**
     * Configures cache error handler.
     * Executes the original method when errors occur such as Redis connection failures.
     */
    @Override
    public CacheErrorHandler errorHandler() {
        return new SimpleCacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, org.springframework.cache.Cache cache, Object key) {
                log.warn("캐시 조회 중 오류 발생: {}. 캐시를 건너뛰고 계속 진행합니다.", exception.getMessage());
            }

            @Override
            public void handleCachePutError(RuntimeException exception, org.springframework.cache.Cache cache, Object key, Object value) {
                log.warn("캐시 저장 중 오류 발생: {}. 캐시를 건너뛰고 계속 진행합니다.", exception.getMessage());
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, org.springframework.cache.Cache cache, Object key) {
                log.warn("캐시 삭제 중 오류 발생: {}. 캐시를 건너뛰고 계속 진행합니다.", exception.getMessage());
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, org.springframework.cache.Cache cache) {
                log.warn("캐시 초기화 중 오류 발생: {}. 캐시를 건너뛰고 계속 진행합니다.", exception.getMessage());
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean(name = "redisCacheManager")
    public CacheManager localCacheManager() {
        log.warn("Redis 연결 실패로 인해 로컬 캐시를 사용합니다.");
        return new ConcurrentMapCacheManager();
    }
} 
