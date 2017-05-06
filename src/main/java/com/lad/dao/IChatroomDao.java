package com.lad.dao;

import com.lad.bo.ChatroomBo;

public interface IChatroomDao extends IBaseDao {

	public ChatroomBo insert(ChatroomBo chatroom);

	public ChatroomBo updateName(ChatroomBo chatroom);
	
	public ChatroomBo updateUsers(ChatroomBo chatroom);

	public ChatroomBo get(Integer chatroomId);
}
