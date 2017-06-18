package com.lad.util;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/6/15
 */
public class RedisUtil {

    public static RedissonClient init(){
        Config config = new Config();
        config.setUseLinuxNativeEpoll(true);
        config.useClusterServers().addNodeAddress("redis://127.0.0.1:6379");
        return Redisson.create(config);
    }
    
}
