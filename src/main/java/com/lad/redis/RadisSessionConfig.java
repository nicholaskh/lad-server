package com.lad.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.spring.session.config.EnableRedissonHttpSession;
import org.springframework.context.annotation.Bean;

/**
 * 功能描述：spring session 加入的redis
 * Version: 1.0
 * Time:2017/6/29
 */
@EnableRedissonHttpSession(maxInactiveIntervalInSeconds = 120*60)
public class RadisSessionConfig {
    
    @Bean
    public RedissonClient redisson() {
        return Redisson.create();
    }

}
