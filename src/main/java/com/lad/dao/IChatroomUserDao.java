package com.lad.dao;

import com.lad.bo.ChatroomUserBo;
import com.mongodb.WriteResult;

import java.util.HashSet;
import java.util.List;

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
     * 查找id里面所有信息
     * @param chatroomid
     * @return
     */
    List<ChatroomUserBo> findByRoomid(String chatroomid);

    /**
     * 查找id里面所有信息
     * @param chatroomid
     * @return
     */
    ChatroomUserBo findByUserAndRoomid(String userid, String chatroomid);

    /**
     * 更新昵称
     * @param id
     * @return
     */
    WriteResult updateNickname(String id, String nickname);

    /**
     *
     * @param userid
     * @param chatroomid
     * @param nickname
     * @return
     */
    WriteResult updateNickname(String userid, String chatroomid, String nickname);

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
    WriteResult deleteChatroom(String userid, String chatroomid);

    /**
     *
     * @param chatroomid
     * @return
     */
    WriteResult deleteChatroom(HashSet<String> userids, String chatroomid);

    /**
     *
     * @param id
     * @return
     */
    WriteResult updateDisturb(String id, boolean isDisturb);

    /**
     *
     * @param id
     * @return
     */
    WriteResult updateShowNick(String id, boolean isShowNick);

    /**
     *
     * @param userid
     * @param chatroomid
     * @param isShowNick
     * @return
     */
    WriteResult updateShowNick(String userid, String chatroomid, boolean isShowNick);

    /**
     * 
     * @param userid
     * @param chatroomid
     * @param isDisturb
     * @return
     */
    WriteResult updateDisturb(String userid, String chatroomid, boolean isDisturb);

}
