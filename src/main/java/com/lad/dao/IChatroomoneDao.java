package com.lad.dao;

import com.lad.bo.ChatroomoneBo;
import com.mongodb.WriteResult;

public interface IChatroomoneDao extends IBaseDao {
	public ChatroomoneBo insert(ChatroomoneBo chatroomoneBo);

	public ChatroomoneBo updateName(ChatroomoneBo chatroomoneBo);

	public WriteResult delete(String userid, String friendid);

	public ChatroomoneBo get(String chatroomId);

	public ChatroomoneBo get(String userid, String friendid);

}
