package com.lad.dao;

import com.lad.bo.ChatroomUserBo;
import com.mongodb.WriteResult;

import java.util.HashMap;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/10/11
 */
public interface IChatroomUserDao {

    /**
     * 插入
     * @param userBo
     * @return
     */
    ChatroomUserBo insert(ChatroomUserBo userBo);

    /**
     * 更具查找
     * @param chatroomid
     * @return
     */
    ChatroomUserBo findByRoomid(String chatroomid);

    /**
     * 更新昵称
     * @param id
     * @return
     */
    WriteResult updateNickname(String id, HashMap<String, String> nicknames);

    /**
     *
     * @param id
     * @return
     */
    WriteResult delete(String id);

    /**
     *
     * @param chatroomid
     * @return
     */
    WriteResult deleteChatroom(String chatroomid);

}
