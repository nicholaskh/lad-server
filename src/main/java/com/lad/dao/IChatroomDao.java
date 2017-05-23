package com.lad.dao;

import com.lad.bo.ChatroomBo;
import com.mongodb.WriteResult;

public interface IChatroomDao extends IBaseDao {

	public ChatroomBo insert(ChatroomBo chatroom);

	public ChatroomBo updateName(ChatroomBo chatroom);
	
	public ChatroomBo updateUsers(ChatroomBo chatroom);

	public ChatroomBo get(String chatroomId);

	public WriteResult delete(String chatroomId);
	
	public ChatroomBo selectByUserIdAndFriendid(String userid, String friendid);
}
