package com.lad.dao;

import com.lad.bo.UserReadHisBo;
import com.mongodb.WriteResult;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2018
 * Version: 1.0
 * Time:2018/2/7
 */
public interface IUserReadHisDao {
    

    UserReadHisBo addUserReadHis(UserReadHisBo hisBo);


    /**
     * 根据类型查找最后一次阅读信息
     * @param userid
     * @param inforType
     * @param module
     * @param className
     * @return
     */
    UserReadHisBo findByType(String userid, int inforType, String module, String className);



    WriteResult updateUserReadHis(String id, String inforid);


    /**
     * 查找最后一次阅读信息
     * @param userid
     * @return
     */
    UserReadHisBo findMyLastRead(String userid);

    
}
