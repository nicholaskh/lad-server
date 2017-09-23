package com.lad.dao;

import com.lad.bo.DynamicMsgBo;
import com.mongodb.WriteResult;

import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/21
 */
public interface IDynamicMsgDao {


    DynamicMsgBo insert(DynamicMsgBo dynamicMsgBo);



    WriteResult delete(String id);

    /**
     * 查找
     * @param tragetid
     * @param type
     * @return
     */
    DynamicMsgBo findByTargetid(String tragetid, int type);

    /**
     * 查看所有好友的动态
     * @return
     */
    List<DynamicMsgBo> findAllFriendsMsg(List<String> friendids, int page, int limit);


    /**
     * 指定好友的动态
     * @param friendid  好友id
     * @return
     */
    List<DynamicMsgBo> findAFriendsMsg(String friendid, int page, int limit);


}
