package com.lad.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lad.bo.ChatroomoneBo;
import com.lad.dao.IChatroomoneDao;
import com.lad.service.IChatroomoneService;
import com.mongodb.WriteResult;

@Service("chatroomoneService")
public class ChatroomoneServiceImpl implements IChatroomoneService {

	@Autowired
	private IChatroomoneDao chatroomoneDao;
	
	public ChatroomoneBo insert(ChatroomoneBo chatroomoneBo) {
		return chatroomoneDao.insert(chatroomoneBo);
	}

	public ChatroomoneBo updateName(ChatroomoneBo chatroomoneBo) {
		return chatroomoneDao.updateName(chatroomoneBo);
	}

	public WriteResult delete(String userid, String friendid) {
		return chatroomoneDao.delete(userid, friendid);
	}

	public ChatroomoneBo get(String chatroomId) {
		return chatroomoneDao.get(chatroomId);
	}

	public ChatroomoneBo get(String userid, String friendid) {
		return chatroomoneDao.get(userid, friendid);
	}

}
