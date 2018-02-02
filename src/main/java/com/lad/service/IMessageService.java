package com.lad.service;

import java.util.List;

import com.lad.bo.MessageBo;
import com.mongodb.WriteResult;

public interface IMessageService extends IBaseService {


	MessageBo insert(MessageBo messageBo);

	MessageBo selectById(String messageId);

	List<MessageBo> findByUserId(String userId, int status, int page, int limit);

	WriteResult deleteMessage(String id);

	WriteResult deleteMessages(List<String> ids);
}
