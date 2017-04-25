package com.lad.service;

import java.util.List;

import com.lad.bo.MessageBo;

public interface IMessageService extends IBaseService {

	public MessageBo insert(MessageBo messageBo);

	public MessageBo selectById(String messageId);

	public List<MessageBo> selectByUserId(String userId);
}
