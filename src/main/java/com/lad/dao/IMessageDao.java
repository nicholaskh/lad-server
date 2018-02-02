package com.lad.dao;

import java.util.List;

import com.lad.bo.MessageBo;
import com.mongodb.WriteResult;

public interface IMessageDao extends IBaseDao {

	public MessageBo insert(MessageBo messageBo);

	public MessageBo selectById(String messageId);
	
	public List<MessageBo> findUnReadByUserId(String userId, int status, int page, int limit);

	WriteResult deleteMessage(String id);

	WriteResult deleteMessages(List<String> ids);

}
