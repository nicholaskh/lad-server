package com.junlenet.mongodb.demo.dao;

import com.junlenet.mongodb.demo.bo.MessageBo;

public interface IMessageDao extends IBaseDao {

	public MessageBo insert(MessageBo messageBo);

	public MessageBo update_thumbsup_ids(MessageBo messageBo);
	
	public MessageBo selectById(String messageId);
}
