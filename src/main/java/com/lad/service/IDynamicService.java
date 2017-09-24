package com.lad.service;

import com.lad.bo.DynamicBackBo;
import com.lad.bo.DynamicBo;
import com.lad.bo.DynamicMsgBo;
import com.lad.bo.DynamicNumBo;
import com.mongodb.WriteResult;

import java.util.HashSet;
import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/23
 */
public interface IDynamicService {

    /**
     * 动态信息添加
     * @param dynamicBo
     * @return
     */
    DynamicBo addDynamic(DynamicBo dynamicBo);
    /**
     * 删除信息
     * @param id
     * @return
     */
    WriteResult deleteDynamic(String id);

    /**
     * 根据id
     * @param id
     * @return
     */
    DynamicBo findDynamicById(String id);
    /**
     * 更新数量
     * @param id
     * @param num
     * @param type 0转发，1评论，2 点赞
     * @return
     */
    WriteResult updateDynamic(String id, int num, int type);

    /**
     * 信息推送表
     * @param msgBo
     * @return
     */
    DynamicMsgBo addDynamicMsg(DynamicMsgBo msgBo);
    WriteResult deleteDynamicMsg(String id);
    /**
     * 查找
     * @param msgid
     * @return
     */
    DynamicMsgBo findByMsgid(String msgid);
    /**
     * 查找
     * @param tragetid
     * @param type
     * @return
     */
    DynamicMsgBo findByTargetid(String tragetid, int type);
    /**
     * 查看所有好友的动态
     * @return
     */
    List<DynamicMsgBo> findAllFriendsMsg(List<String> friendids, int page, int limit);
    /**
     * 指定好友的动态
     * @param friendid  好友id
     * @return
     */
    List<DynamicMsgBo> findOneFriendMsg(String friendid, int page, int limit);
    /**
     * 动态黑名单添加
     * @param backBo
     * @return
     */
    DynamicBackBo addDynamicBack(DynamicBackBo backBo);
    /**
     * 查询黑名单
     * @param userid
     * @return
     */
    DynamicBackBo findBackByUserid(String userid);
    /**
     * 查询谁的黑名单有我
     * @param userid
     * @return
     */
    List<DynamicBackBo> findWhoBackMe(String userid);
    /**
     * 更新 我不看谁 黑名单
     * @param id
     * @param notSeeBacks
     * @return
     */
    WriteResult updateBackNotSee(String id, HashSet<String> notSeeBacks);
    /**
     * 更新 不让谁看我黑名单
     * @param id
     * @param notAllowBacks
     * @return
     */
    WriteResult updateBackNotAllow(String id, HashSet<String> notAllowBacks);

    /**
     * 动态信息数字
     * @param numBo
     * @return
     */
    DynamicNumBo addNum(DynamicNumBo numBo);

    /**
     * 动态信息
     * @param userid
     * @return
     */
    DynamicNumBo findNumByUserid(String userid);

    /**
     * 更新
     * @param id
     * @param addNum
     * @return
     */
    WriteResult updateNumbers(String id, int addNum);



}
