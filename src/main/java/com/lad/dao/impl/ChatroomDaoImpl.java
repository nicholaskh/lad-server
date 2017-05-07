package com.lad.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.lad.bo.ChatroomBo;
import com.lad.bo.HomepageBo;
import com.lad.bo.MessageBo;
import com.lad.dao.IChatroomDao;

@Repository("chatroomDao")
public class ChatroomDaoImpl implements IChatroomDao {
	@Autowired
	private MongoTemplate mongoTemplate;

	public ChatroomBo insert(ChatroomBo chatroom) {
		mongoTemplate.insert(chatroom);
		return chatroom;
	}

	public ChatroomBo updateName(ChatroomBo chatroom) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(chatroom.getId()));
		Update update = new Update();
		update.set("name", chatroom.getName());
		mongoTemplate.updateFirst(query, update, ChatroomBo.class);
		return chatroom;
	}

	public ChatroomBo get(String chatroomId) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(chatroomId));
		return mongoTemplate.findOne(query, ChatroomBo.class);
	}

	public ChatroomBo updateUsers(ChatroomBo chatroom) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(chatroom.getId()));
		Update update = new Update();
		update.set("users", chatroom.getUsers());
		mongoTemplate.updateFirst(query, update, ChatroomBo.class);
		return chatroom;
	}

}
