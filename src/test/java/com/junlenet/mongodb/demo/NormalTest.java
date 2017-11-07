package com.junlenet.mongodb.demo;

import com.lad.bo.InforSubscriptionBo;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.cache.CacheConfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/23
 */
public class NormalTest {

    @Test
    public void RedisTest(){
        Config config = new Config();
        config.useSingleServer().setAddress("127.0.0.1:6379");
        RedissonClient client = Redisson.create(config);
        System.out.println("redis 初始化连接成功");

        Map<String, CacheConfig> configMap = new HashMap<>();
        // 创建一个名称为"testCache"的缓存，过期时间ttl为24秒钟，同时最长空闲时maxIdleTime为12秒钟。
        configMap.put("testMap", new CacheConfig(120*60*1000, 12*60*1000));

        RMapCache<String ,Object> mapCache = client.getMapCache("testMap");

        LinkedHashSet<String> hashSet = new LinkedHashSet<>();
        InforSubscriptionBo mySub = new InforSubscriptionBo();
        mySub.setSubscriptions(hashSet);
        hashSet.add("职能1");
        hashSet.add("职能2");
        hashSet.add("职能3");
        hashSet.add("职能4");
        hashSet.add("职能5");
        hashSet.add("职能3");
        mapCache.put("name", hashSet);


        Object type = mapCache.get("name");
        LinkedHashSet<String> sets = (LinkedHashSet<String>) type;

        String[] arrs = "123,12333,4566,891231".split(",");
        Collections.addAll(hashSet, arrs);

        for (String set : mySub.getSubscriptions()) {
            System.out.println("=========== : " + set);
        }

    }




}
