package com.lad.service;

import com.lad.bo.ChatroomBo;
import com.mongodb.WriteResult;

public interface IChatroomService extends IBaseService {
	
	public ChatroomBo insert(ChatroomBo chatroom);

	public ChatroomBo updateName(ChatroomBo chatroom);
	
	public ChatroomBo updateUsers(ChatroomBo chatroom);

	public ChatroomBo get(String chatroomId);
	
	public WriteResult delete(String chatroomId);
	
	public WriteResult setTop(String chatroomId);
	
	public WriteResult cancelTop(String chatroomId);
	
	public ChatroomBo selectByUserIdAndFriendid(String userid, String friendid);
}
