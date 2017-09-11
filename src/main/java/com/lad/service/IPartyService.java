package com.lad.service;

import com.lad.bo.PartyBo;
import com.mongodb.WriteResult;

import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/7
 */
public interface IPartyService {

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
     * 查找我发起的聚会
     * @param createid
     * @return
     */
    List<PartyBo> findByCreate(String createid, String start_id, int limit);

    /**
     * 查找我参与的聚会
     * @return
     */
    List<PartyBo> findByMyJoin(String userid, String start_id, int limit);

    /**
     * 查找聚会
     * @param id
     * @return
     */
    PartyBo findById(String id);

}
