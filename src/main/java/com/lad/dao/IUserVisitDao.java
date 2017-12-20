package com.lad.dao;

import com.lad.bo.UserVisitBo;
import com.mongodb.WriteResult;

import java.util.Date;
import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/12/18
 */
public interface IUserVisitDao {

    /**
     * 添加访问记录
     * @param userVisitBo
     * @return
     */
    UserVisitBo addUserVisit(UserVisitBo userVisitBo);

    /**
     * 更新访问时间
     * @param id
     * @param date
     * @return
     */
    WriteResult updateUserVisit(String id, Date date);

    /**
     * 我访问的页面记录
     * @param userid
     * @param page
     * @param limit
     * @return
     */
    List<UserVisitBo> visitFromMeList(String userid, int type, int page, int limit);

    /**
     * 访问我的页面记录
     * @param userid
     * @param page
     * @param limit
     * @return
     */
    List<UserVisitBo> visitToMeList(String userid, int type,int page, int limit);


    /**
     * 删除访问记录
     * @param id
     * @return
     */
    WriteResult deleteUserVisit(String id);


    /**
     * 查找用户访问信息
     * @param ownerid
     * @param visitid
     * @return
     */
    UserVisitBo findUserVisit(String ownerid, String visitid, int type);

}
