package com.lad.dao;

import com.lad.bo.PartyNoticeBo;
import com.mongodb.WriteResult;

import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/11/25
 */
public interface IPartyNoticeDao {

    /**
     * 添加通知
     * @param noticeBo
     * @return
     */
    PartyNoticeBo addPartyNotice(PartyNoticeBo noticeBo);

    /**
     *
     * @param id
     * @return
     */
    PartyNoticeBo findNoticeById(String id);

    /**
     *
     * @param id
     * @return
     */
    WriteResult deleteNotice(String id);

    /**
     *
     * @param partyid
     * @param page
     * @param limit
     * @return
     */
    List<PartyNoticeBo> findByPartyid(String partyid, int page, int limit);

    /**
     * 
     * @param userid
     * @param page
     * @param limit
     * @return
     */
    List<PartyNoticeBo> findByUserid(String userid, int page, int limit);


    /**
     * 添加通知
     * @param partyid
     * @return
     */
    PartyNoticeBo findPartyNotice(String partyid);
    


}
