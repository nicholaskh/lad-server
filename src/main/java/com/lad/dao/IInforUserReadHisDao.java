package com.lad.dao;

import com.lad.bo.InforUserReadHisBo;
import com.mongodb.WriteResult;

import java.util.Date;

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
     * 更新当前用户阅读记录
     * @param id
     * @param currentDate
     * @return
     */
    WriteResult updateUserReadHis(String id, Date currentDate);




}
