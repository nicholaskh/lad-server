package com.lad.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lad.bo.ChatroomBo;
import com.lad.dao.IChatroomDao;
import com.lad.service.IChatroomService;

@Service("chatroomService")
public class ChatroomServiceImpl implements IChatroomService {

	@Autowired
	private IChatroomDao chatroomDao;
	
	public ChatroomBo insert(ChatroomBo chatroom) {
		return chatroomDao.insert(chatroom);
	}

	public ChatroomBo updateName(ChatroomBo chatroom) {
		return chatroomDao.updateName(chatroom);
	}

	public ChatroomBo get(Integer chatroomId) {
		return chatroomDao.get(chatroomId);
	}

	public ChatroomBo updateUsers(ChatroomBo chatroom) {
		return chatroomDao.updateUsers(chatroom);
	}

}
