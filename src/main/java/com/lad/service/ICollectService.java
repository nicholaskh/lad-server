package com.lad.service;

import com.lad.bo.CollectBo;
import com.mongodb.WriteResult;

import java.util.List;

public interface ICollectService {
	
	/**
	 * 收藏聊天记录
	 * @param collectBo 聊天记录
	 * @return 聊天信息
	 */
	CollectBo saveChat(CollectBo collectBo);
	
	/**
	 * 查找我收藏聊天记录
	 * @param userid 聊天记录
	 * @return 聊天信息
	 */
	List<CollectBo> findMyChat(String userid);
	
	WriteResult delete(String chatId);

	/**
	 * 
	 * @param userid
	 * @param start_id
	 * @param limit
	 * @return
	 */
	List<CollectBo> findChatByUserid(String userid, String start_id, int limit);

}
