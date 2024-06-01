package com.lab.players.config;

import com.google.common.cache.CacheBuilder;
import com.lab.players.cache.GuavaCache;
import com.lab.players.cache.WorkingKeyGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer {

    public static final String SERVICE_FIND_ALL = "serviceFindAll";
    public static final String SERVICE_FIND_BY_ID = "serviceFindById";
    public static final String SERVICE_FIND_ALL_PAGEABLE = "serviceFindAllPageable";

    /**
     * Cache manager.
     * Prefer Caffeine to Guava's caching API
     *
     * @return CacheManager.
     */
    @Bean
    @Override
    public CacheManager cacheManager() {
        log.info("Initializing simple Guava Cache manager.");
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        GuavaCache findAllCache = new GuavaCache(SERVICE_FIND_ALL, CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build());
        GuavaCache findByIdCache = new GuavaCache(SERVICE_FIND_BY_ID, CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build());
        GuavaCache findAllCachePageable = new GuavaCache(SERVICE_FIND_ALL_PAGEABLE, CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build());
        cacheManager.setCaches(Arrays.asList(findAllCache, findByIdCache, findAllCachePageable));
        return cacheManager;
    }

    @Bean(name = "simpleKeyGenerator")
    public KeyGenerator getSimpleKeyGenerator() {
        return new SimpleKeyGenerator();
    }

    @Bean(name = "workingKeyGenerator")
    public KeyGenerator getWorkingKeyGenerator() {
        return new WorkingKeyGenerator();
    }
}
