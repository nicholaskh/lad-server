package com.lad.redis;

import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述：
 * Version: 1.0
 * Time:2017/8/13
 */
@Configuration
@EnableCaching
public class RedisCache {

    @Bean
    CacheManager cacheManager() {
        RedisServer redisServer = new RedisServer();
        Map<String, CacheConfig> config = new HashMap<>();
        // 创建一个名称为"testCache"的缓存，过期时间ttl为24秒钟，同时最长空闲时maxIdleTime为12秒钟。
        config.put("testMap", new CacheConfig(120*60*1000, 12*60*1000));
        config.put("testCache", new CacheConfig(120*60*1000, 12*60*1000));
        return new RedissonSpringCacheManager(redisServer.getClient(), config);
    }

}
