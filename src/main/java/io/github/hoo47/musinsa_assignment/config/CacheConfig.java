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
 * 캐시 설정을 담당하는 클래스
 * Redis 캐시를 사용하여 성능을 향상시킵니다.
 * 테스트 환경에서는 적용되지 않음
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
     * Redis 연결 팩토리를 설정합니다.
     * 명시적인 타임아웃 설정으로 연결 실패 시 빠르게 오류를 감지합니다.
     *
     * @return Redis 연결 팩토리
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
        
        // 명시적 타임아웃 설정 및 클라이언트 이름 설정
        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(connectionTimeout))
                .clientName(clientName)
                .build();
        
        return new LettuceConnectionFactory(config, clientConfig);
    }
    
    /**
     * Redis 캐시 매니저를 설정합니다.
     * 커머스 서비스 특성상 데이터가 자주 변경될 수 있으므로 짧은 TTL을 사용합니다.
     * 
     * @param redisConnectionFactory Redis 연결 팩토리
     * @return 설정된 Redis 캐시 매니저
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // Redis 캐시 기본 설정
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(timeToLive))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())
                )
                .disableCachingNullValues();
        
        // 개별 캐시 설정 - 커머스 서비스에 맞게 TTL 설정
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withCacheConfiguration("priceSummaryCache", 
                        defaultConfig.entryTtl(Duration.ofSeconds(30))) // 30초
                .withCacheConfiguration("categoryPricingCache", 
                        defaultConfig.entryTtl(Duration.ofSeconds(20))) // 20초
                .withCacheConfiguration("brandLowestPriceCache", 
                        defaultConfig.entryTtl(Duration.ofSeconds(60))) // 1분
                .withCacheConfiguration("priceInfoCache", 
                        defaultConfig.entryTtl(Duration.ofSeconds(120))) // 2분 - 새로 추가된 캐시
                .build();
    }
    
    /**
     * Redis 템플릿을 설정합니다.
     * 
     * @param redisConnectionFactory Redis 연결 팩토리
     * @return 설정된 Redis 템플릿
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
     * 캐시 에러 핸들러를 설정합니다.
     * Redis 연결 실패 등의 오류가 발생하면 원래 메서드를 실행합니다.
     */
    @Override
    public CacheErrorHandler errorHandler() {
        return new CustomCacheErrorHandler();
    }
    
    /**
     * 캐시 에러 처리를 위한 커스텀 핸들러
     * 캐시 조회/갱신 실패 시 원래 메서드를 실행하도록 합니다.
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
