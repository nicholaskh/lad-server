package com.lad.service;

import com.lad.bo.InforSubscriptionBo;
import com.lad.scrapybo.InforBo;
import com.mongodb.WriteResult;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/8/1
 */
public interface IInforService {

    /**
     * 获取一级分类 类型名称
     * @param module
     * @return
     */
    List<InforBo> findGroups(String module);

    /**
     * 一级分类下咨询信息列表
     * @param module
     * @return
     */
    List<InforBo> findGroupInfos(String module);

    /**
     * 二级分类 类型名称
     * @param className
     * @return
     */
    List<InforBo> findGroupClass(String className);

    /**
     * 二级分类下咨询信息列表
     * @param className
     * @return
     */
    List<InforBo> findClassInfos(String className);


    InforBo findById(String id);



    List<InforSubscriptionBo> findMySubs(String userid);

    /**
     * 订阅一级分类 类型
     * @param groupName  一级分类名称
     * @param isAdd  true, 添加订阅； false 取消订阅
     */
    void subscriptionGroup(String groupName, boolean isAdd);


    List<InforSubscriptionBo> findMyCollects(String userid);

    /**
     * 收藏资讯信息
     * @param id  咨询id
     * @param isAdd  true, 添加收藏； false 取消收藏
     */
    void collectInfor(String id, boolean isAdd);


    /**
     * 获取咨询阅读数
     * @param inforid
     * @return
     */
    Long findReadNum(String inforid);


    InforSubscriptionBo insertSub(InforSubscriptionBo inforSubscriptionBo);

    /**
     *  更新咨询分类订阅
     * @param userid
     * @param subscriptions
     * @return
     */
    WriteResult updateSub(String userid, LinkedHashSet<String> subscriptions);

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
