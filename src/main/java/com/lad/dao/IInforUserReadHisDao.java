package com.lad.dao;

import com.lad.bo.InforUserReadHisBo;
import com.mongodb.WriteResult;

import java.util.Date;
import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/10/24
 */
public interface IInforUserReadHisDao {


    InforUserReadHisBo addUserReadHis(InforUserReadHisBo userReadHisBo);

    /**
     * 查找用户半年内指定类型和分类的阅读记录
     * @param userid
     * @param type
     * @param module
     * @param halfTime
     * @return
     */
    InforUserReadHisBo findUserReadHis(String userid, int type, String module, Date halfTime);

    /**
     * 查找用户半年之前的分类阅读记录
     * @param userid
     * @param halfTime
     * @return
     */
    List<InforUserReadHisBo> findUserReadHisBeforeHalf(String userid, Date halfTime);

    /**
     * 更新当前用户阅读记录
     * @param id
     * @param currentDate
     * @return
     */
    WriteResult updateUserReadHis(String id, Date currentDate);


    /**
     * 查找用户半年内指定类型和分类的阅读记录
     * @param userid
     * @param type
     * @param module
     * @return
     */
    InforUserReadHisBo findByReadHis(String userid, int type, String module);


}
