package com.lad.redis;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.RootLogger;
import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.config.Config;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 功能描述：redis服务
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/6/28
 */
@Component
public class RedisServer {

    private static final Logger logger = RootLogger.getLogger(RedisServer.class);

    private RedissonClient client;
    /**
     * 获取信息
     * @return
     */
    public RedissonClient getClient(){
        if (client == null) {
          init();
        }
        return client;
    }

    /**
     * 初始化RedissonClient
     */
    @PostConstruct
    public void init(){
        Config config = new Config();
        config.useSingleServer().setAddress("127.0.0.1:6379");
        client = Redisson.create(config);
        logger.info("redis 初始化连接成功");
    }

    /**
     * 获取锁
     * @param lockName
     * @return
     */
    public RLock getRLock(String lockName){
        return client.getLock(lockName);
    }

    /**
     * 获取公平锁
     * @param lockName
     * @return
     */
    public RLock getFireLock(String lockName){
        return client.getFairLock(lockName);
    }

    /**
     * 获取session map
     * @param key
     * @return
     */
    public RMapCache getCacheMap(String key){
        return client.getMapCache(key);
    }

    /**
     * 关闭
     */
    public void shutdown(){
        client.shutdown();
        logger.info("redisson client 关闭");
    }

    /**
     * 获取集合
     * @param objectName
     * @return
     */
    public <V> RSet<V> getRSet(String objectName){
        return client.getSet(objectName);
    }

    /**
     * 获取列表
     * @param objectName
     * @return
     */
    public <V> RList<V> getRList(String objectName){
        return client.getList(objectName);
    }


}
