package com.lad.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lad.bo.ChatinfoBo;
import com.lad.dao.ChatinfoDao;
import com.lad.service.IChatService;
import com.mongodb.WriteResult;

@Service("chatService")
public class ChatServiceImpl implements IChatService {
	
	@Autowired
	private ChatinfoDao chatinfoDao;
	
	
	@Override
	public ChatinfoBo saveChat(ChatinfoBo chatinfo) {
		return chatinfoDao.save(chatinfo);
	}


	@Override
	public List<ChatinfoBo> findMyChat(String userid) {
		return chatinfoDao.findChatByUserid(userid);
	}


	@Override
	public WriteResult delete(String chatId) {
		return chatinfoDao.delete(chatId);
	}

}
