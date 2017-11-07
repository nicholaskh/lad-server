package com.lad.dao;

import com.lad.bo.PartyUserBo;
import com.mongodb.WriteResult;

import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/8/27
 */
public interface IPartyUserDao {

    PartyUserBo insert(PartyUserBo partyUserBo);


    WriteResult refuse(String id, String info);


    WriteResult apply(String id, String info);


    List<PartyUserBo> findByParty(String partyid, int status);


    List<PartyUserBo>  findByUserid(String userid, int status);

    /**
     * 退出聚会
     * @param partyid
     * @return
     */
    WriteResult outParty(String partyid, String userid);

    /**
     * 报名详情查看
     * @param partyid
     * @return
     */
    List<PartyUserBo> findByPartyUsers(String partyid);

    /**
     * 参与的聚会
     * @return
     */
    List<PartyUserBo> findPartyByUserid(String userid, int page, int limit);


    /**
     * 收藏或取消收藏聚会
     * @param partyid
     * @return
     */
    WriteResult collectParty(String partyid, String userid, boolean isCollect);

    /**
     * 查找当前用户聚会参与信息
     * @param partyid
     * @return
     */
    PartyUserBo findPartyUser(String partyid, String userid);

    /**
     * 查找当前用户聚会参与信息,不需要判断是否已经删除
     * @param partyid
     * @return
     */
    PartyUserBo findPartyUserIgnoreDel(String partyid, String userid);


    /**
     * 退出聚会
     * @param partyid
     * @return
     */
    WriteResult deleteMulit(String partyid);

}
