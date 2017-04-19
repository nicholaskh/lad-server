package com.junlenet.mongodb.demo.service;

import java.util.List;

import com.junlenet.mongodb.demo.bo.MessageBo;

public interface IMessageService extends IBaseService {

	public MessageBo insert(MessageBo messageBo);

	public MessageBo selectById(String messageId);

	public List<MessageBo> selectByUserId(String userId);
}
