package io.github.hoo47.musinsa_assignment.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 캐시 설정을 담당하는 클래스
 * 현재는 메모리 캐시를 사용하지만, 필요에 따라 Redis 등 다른 캐시로 대체 가능
 * 테스트 환경에서는 적용되지 않음
 */
@Configuration
@Profile("!test")
@EnableCaching
public class CacheConfig {
    
    /**
     * 기본 캐시 매니저를 설정합니다.
     * 메모리 기반의 ConcurrentMapCacheManager를 사용합니다.
     * 
     * @return 설정된 캐시 매니저
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                "priceSummaryCache", 
                "categoryPricingCache", 
                "brandLowestPriceCache"
        );
    }
} 
