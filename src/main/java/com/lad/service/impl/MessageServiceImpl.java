package com.lad.service.impl;

import java.util.List;

import com.mongodb.WriteResult;
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

	@Override
	public List<MessageBo> findByUserId(String userId, int status, int page, int limit) {
		return messageDao.findUnReadByUserId(userId, status, page, limit);
	}

	@Override
	public WriteResult deleteMessage(String id) {
		return messageDao.deleteMessage(id);
	}

	@Override
	public WriteResult deleteMessages(List<String> ids) {
		return messageDao.deleteMessages(ids);
	}

	@Override
	public WriteResult updateMessage(String id, int status) {
		return messageDao.updateMessage(id, status);
	}

	@Override
	public WriteResult betchUpdateMessage(List<String> ids, int status) {
		return messageDao.betchUpdateMessage(ids, status);
	}

	@Override
	public List<MessageBo> findUnReadByNoteId(String noteid, int status) {
		return messageDao.findUnReadByNoteId(noteid, status);
	}

	@Override
	public MessageBo findMessageBySource(String sourceid, int type) {
		return messageDao.findMessageBySource(sourceid, type);
	}

	@Override
	public WriteResult deleteMessageBySource(String sourceid, int type) {
		return messageDao.deleteMessageBySource(sourceid, type);
	}

	@Override
	public WriteResult deleteMessageByNoteid(String noteid, int type) {
		return messageDao.deleteMessageByNoteid(noteid, type);
	}

	@Override
	public List<MessageBo> findUnReadByMyUserid(String userid, String circleid) {
		return messageDao.findUnReadByMyUserid(userid, circleid);
	}

	@Override
	public WriteResult clearUnReadByMyUserid(String userid, String circleid) {
		return messageDao.clearUnReadByMyUserid(userid, circleid);
	}
}
