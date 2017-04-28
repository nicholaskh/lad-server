package com.lad.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lad.bo.MessageBo;
import com.lad.dao.IMessageDao;
import com.lad.service.IMessageService;

@Service("messageService")
public class MessageServiceImpl implements IMessageService {

	@Autowired
	private IMessageDao messageDao;

	public MessageBo insert(MessageBo messageBo) {
		return messageDao.insert(messageBo);
	}

	public MessageBo selectById(String messageId) {
		return messageDao.selectById(messageId);
	}

	public List<MessageBo> selectByUserId(String userId) {
		return messageDao.selectByUserId(userId);
	}

	public List<MessageBo> selectByUserIdPaged(String startId, boolean gt, int limit, String userId) {
		return messageDao.selectByUserIdPaged(startId, gt, limit, userId);
	}

}
