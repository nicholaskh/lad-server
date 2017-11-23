package com.lad.dao;

import com.lad.bo.PartyBo;
import com.mongodb.WriteResult;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/8/20
 */
public interface IPartyDao {

    /**
     * 添加聚会
     * @param partyBo
     * @return
     */
    PartyBo insert(PartyBo partyBo);

    /**
     * 修改聚会
     * @param partyBo
     * @return
     */
    WriteResult update(PartyBo partyBo);

    /**
     * 删除聚会
     * @param id
     * @return
     */
    WriteResult delete(String id);

    /**
     * 查找聚会
     * @param id
     * @return
     */
    PartyBo findById(String id);

    /**
     * 查找我发起的聚会
     * @param createid
     * @return
     */
    List<PartyBo> findByCreate(String createid, int page, int limit);

    /**
     * 查找我参与的聚会
     * @return
     */
    List<PartyBo> findByMyJoin(String userid, int page, int limit);

    /**
     * 更新用户
     * @param id
     * @param users
     * @return
     */
    WriteResult updateUser(String id, List<String> users, int userNum);

    /**
     * 更新已拒绝的用户
     * @param id
     * @return
     */
    WriteResult updateRefus(String id, LinkedHashSet<String> refuses);


    /**
     * 更新访问
     * @param id
     * @return
     */
    WriteResult updateVisit(String id);

    /**
     * 更新分享
     * @param id
     * @return
     */
    WriteResult updateShare(String id, int num);

    /**
     * 更新分享
     * @param id
     * @return
     */
    WriteResult updateCollect(String id, int num);

    /**
     * 更新举报
     * @param id
     * @return
     */
    WriteResult updateReport(String id, int num);

    /**
     * 查找我申请的聚会
     * @return
     */
    List<PartyBo> findByMyApply(String userid, int page, int limit);

    /**
     * 更新群聊
     * @return
     */
    WriteResult updateChatroom(String partyid, String chatroomid);


    /**
     * 查找圈子里所有聚会
     * @return
     */
    List<PartyBo> findByCircleid(String circleid, int page, int limit);

    /**
     * 退出圈子
     * @return
     */
    WriteResult outParty(String id, String userid);

    /**
     * 聚会状态修改
     * @param id
     * @return
     */
    WriteResult updatePartyStatus(String id, int status);

}
