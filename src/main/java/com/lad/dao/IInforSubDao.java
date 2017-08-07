package com.lad.dao;

import com.lad.bo.InforSubscriptionBo;
import com.mongodb.WriteResult;

import java.util.LinkedHashSet;
import java.util.LinkedList;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/8/5
 */
public interface IInforSubDao {


    InforSubscriptionBo insert(InforSubscriptionBo inforSubscriptionBo);

    /**
     *  更新咨询分类订阅
     * @param userid
     * @param subscriptions
     * @return
     */
    WriteResult updateSub(String userid, LinkedList<String> subscriptions);

    /**
     * 更新咨询收藏
     * @param userid
     * @param collects
     * @return
     */
    WriteResult updateCollect(String userid, LinkedHashSet<String> collects);

    /**
     *  查询个人资讯订阅情况
     * @param userid
     * @return
     */
    InforSubscriptionBo findByUserid(String userid);
}
