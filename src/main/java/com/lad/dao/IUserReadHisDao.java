package com.lad.dao;

import com.lad.bo.UserReadHisBo;
import com.mongodb.WriteResult;

import java.util.List;

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


    /**
     * 根据类型查找最后一次阅读信息
     * @param userid
     * @return
     */
    UserReadHisBo findByInforid(String userid, String inforid);

    /**
     * 根据类型查找最后一次阅读信息
     * @param id
     * @return
     */
    WriteResult updateUserReadHis(String id);


    /**
     * 查找最后一次阅读信息
     * @param userid
     * @return
     */
    WriteResult deleteUserReadHis(String userid, int inforType);

    /**
     * 删除多余的阅读历史信息
     * @param userid
     * @return
     */
    WriteResult deleteUserReadHis(List<String> removeIds);

    /**
     * 查找我的阅读历史
     * @param userid
     * @return
     */
    List<UserReadHisBo> findByUserAndInfor(String userid, int inforType);

    
}
