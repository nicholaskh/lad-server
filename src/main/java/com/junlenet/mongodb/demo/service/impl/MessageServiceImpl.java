package com.junlenet.mongodb.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.junlenet.mongodb.demo.bo.MessageBo;
import com.junlenet.mongodb.demo.dao.IMessageDao;
import com.junlenet.mongodb.demo.service.IMessageService;

@Service("messageService")
public class MessageServiceImpl implements IMessageService {

	@Autowired
	private IMessageDao messageDao;

	public MessageBo insert(MessageBo messageBo) {
		return messageDao.insert(messageBo);
	}

	public MessageBo update_thumbsup_ids(MessageBo messageBo) {
		return messageDao.update_thumbsup_ids(messageBo);
	}

	public MessageBo selectById(String messageId) {
		return messageDao.selectById(messageId);
	}

}
