package com.lad.service;

import com.lad.bo.ChatroomoneBo;
import com.mongodb.WriteResult;

public interface IChatroomoneService extends IBaseService {
	public ChatroomoneBo insert(ChatroomoneBo chatroomoneBo);

	public ChatroomoneBo updateName(ChatroomoneBo chatroomoneBo);

	public WriteResult delete(String userid, String friendid);

	public ChatroomoneBo get(String chatroomId);

	public ChatroomoneBo get(String userid, String friendid);
}
