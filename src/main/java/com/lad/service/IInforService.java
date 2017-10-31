package com.lad.service;

import com.lad.bo.InforReadNumBo;
import com.lad.bo.InforSubscriptionBo;
import com.lad.scrapybo.BroadcastBo;
import com.lad.scrapybo.InforBo;
import com.lad.scrapybo.SecurityBo;
import com.lad.scrapybo.VideoBo;
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
     * @return
     */
    List<InforBo> findAllGroups() ;

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
     * @param className  文章分类
     * @param createTime  文章的发布时间
     * @param limit   每页显示数量
     * @return
     */
    List<InforBo> findClassInfos(String className, String createTime, int limit);


    InforBo findById(String id);



    InforSubscriptionBo findMySubs(String userid);

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
    WriteResult updateSub(String userid, int type, LinkedHashSet<String> subscriptions);

    /**
     *
     * @param userid
     * @param securitys
     * @return
     */
    WriteResult updateSecuritys(String userid, LinkedHashSet<String> securitys);

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


    InforReadNumBo findReadByid(String inforid);


    InforReadNumBo addReadNum(InforReadNumBo readNumBo);

    void updateReadNum(String inforid);

    void updateComment(String inforid, int number);

    void updateThumpsub(String inforid, int number);
    /**
     * 获取安全类型
     * @return
     */
    List<SecurityBo> findSecurityTypes();
    /**
     * 根据类型查找
     * @param typeName
     * @param createTime
     * @param limit
     * @return
     */
    List<SecurityBo> findSecurityByType(String typeName, String createTime, int limit);
    /**
     * 根据城市查询
     * @param typeName
     * @param createTime
     * @param limit
     * @return
     */
    List<SecurityBo> findSecurityByCity(String typeName, String createTime, int limit);

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    SecurityBo findSecurityById(String id);

    /**
     * 更具id查找
     * @param id
     * @return
     */
    BroadcastBo findBroadById(String id);

    /**
     * 查找所有类型
     * @return
     */
    List<BroadcastBo> selectBroadGroups();

    /**
     * 根据分类查找
     * @param groupName
     * @return
     */
    List<BroadcastBo> selectBroadClassByGroups(String groupName);

    /**
     * 分页查找
     * @param groupName
     * @param page
     * @param limit
     * @return
     */
    List<BroadcastBo> findBroadByPage(String groupName, int page, int limit);

    List<VideoBo> findVideoByPage(String groupName, int page, int limit);

    VideoBo findVideoById(String id);

    List<VideoBo> selectVdeoGroups();

    List<VideoBo> selectVideoClassByGroups(String groupName);

    /**
     * 更新缩略图
     * @param id
     * @param pic
     * @return
     */
    WriteResult updateVideoPicById(String id, String pic);

    /**
     * 首页推荐
     * @return
     */
    List<InforBo> homeHealthRecom(int limit);

    /**
     * 首页推荐
     * @return
     */
    List<InforBo> userHealthRecom(String userid, int limit);


    List<SecurityBo> findSecurityByLimit(int limit);


    List<BroadcastBo> findRadioByLimit(int limit);


    List<VideoBo> findVideoByLimit(int limit);
}
