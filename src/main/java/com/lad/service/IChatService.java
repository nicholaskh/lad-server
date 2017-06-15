package com.lad.service;

import java.util.List;

import com.lad.bo.ChatinfoBo;
import com.mongodb.WriteResult;

public interface IChatService {
	
	/**
	 * 收藏聊天记录
	 * @param chatinfo 聊天记录
	 * @return 聊天信息
	 */
	ChatinfoBo saveChat(ChatinfoBo chatinfo);
	
	/**
	 * 查找我收藏聊天记录
	 * @param userid 聊天记录
	 * @return 聊天信息
	 */
	List<ChatinfoBo> findMyChat(String userid);
	
	WriteResult delete(String chatId);

}
