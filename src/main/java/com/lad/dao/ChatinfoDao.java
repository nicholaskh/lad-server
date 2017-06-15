package com.lad.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.lad.bo.ChatinfoBo;
import com.mongodb.WriteResult;

/**
 * 收藏聊天记录
 */
@Repository
public class ChatinfoDao {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public ChatinfoBo save(ChatinfoBo chatinfo){
		mongoTemplate.save(chatinfo);
		return chatinfo;
	}
	
	public List<ChatinfoBo> findChatByUserid(String userid){
		Query query = new Query();
		query.addCriteria(new Criteria("userid").is(userid));
		query.addCriteria(new Criteria("deleted").is(0));
		return mongoTemplate.find(query, ChatinfoBo.class);
	}
	/**
	 * 删除单条聊天记录
	 * @param chatid
	 * @return
	 */
	public WriteResult delete(String chatid) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(chatid));
		query.addCriteria(new Criteria("deleted").is(0));
		Update update = new Update();
		update.set("deleted", 1);
		return mongoTemplate.updateFirst(query, update, ChatinfoBo.class);
	}

}
