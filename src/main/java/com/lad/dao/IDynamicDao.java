package com.lad.dao;

import com.lad.bo.DynamicBo;
import com.mongodb.WriteResult;

import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/21
 */
public interface IDynamicDao {

    /**
     * 添加
     * @param dynamicBo
     * @return
     */
    DynamicBo insert(DynamicBo dynamicBo);

    /**
     * 删除
     * @param id
     * @return
     */
    WriteResult delete(String id);

    /**
     * 根据id
     * @param id
     * @return
     */
    DynamicBo findById(String id);

    /**
     * 更新数量
     * @param id
     * @param num
     * @param type
     * @return
     */
    WriteResult update(String id, int num, int type);


    /**
     * 查找
     * @param msgid
     * @return
     */
    DynamicBo findByMsgid(String msgid);

    /**
     * 查看所有好友的动态
     * @return
     */
    List<DynamicBo> findAllFriendsMsg(List<String> friendids, int page, int limit);


    /**
     * 指定好友的动态
     * @param friendid  好友id
     * @return
     */
    List<DynamicBo> findAFriendsMsg(String friendid, int page, int limit);

}
