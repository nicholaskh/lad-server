package com.lad.service;

import com.lad.bo.ChatroomBo;

public interface IChatroomService extends IBaseService {
	
	public ChatroomBo insert(ChatroomBo chatroom);

	public ChatroomBo updateName(ChatroomBo chatroom);
	
	public ChatroomBo updateUsers(ChatroomBo chatroom);

	public ChatroomBo get(Integer chatroomId);
}
