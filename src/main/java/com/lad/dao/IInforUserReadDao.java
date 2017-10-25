package com.lad.dao;

import com.lad.bo.InforUserReadBo;
import com.mongodb.WriteResult;

import java.util.LinkedHashSet;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/10/24
 */
public interface IInforUserReadDao {

    InforUserReadBo addUserRead(InforUserReadBo userReadBo);


    InforUserReadBo findUserReadByUserid(String userid);

    /**
     * 更具类型修改
     * @param id
     * @param type
     * @param modules
     * @return
     */
    WriteResult updateUserRead(String id, int type, LinkedHashSet<String> modules);


    WriteResult deleteUserRead(String id);

}
