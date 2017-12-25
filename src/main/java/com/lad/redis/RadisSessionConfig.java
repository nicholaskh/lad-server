package com.lad.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 功能描述：spring session 加入的redis
 * Version: 1.0
 * Time:2017/6/29
 */
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 365*24*3600)
public class RadisSessionConfig {

    @Bean
    public JedisConnectionFactory connectionFactory() {
        return new JedisConnectionFactory();
    }
}
