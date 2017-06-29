package com.lad.redis;

import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

/**
 * 功能描述：启动器
 * Version: 1.0
 * Time:2017/6/29
 */
public class RedisSessionInitializer extends AbstractHttpSessionApplicationInitializer {

    public RedisSessionInitializer(){
        super(RadisSessionConfig.class);
    }

}
