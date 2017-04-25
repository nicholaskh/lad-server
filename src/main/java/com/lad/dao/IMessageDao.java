package com.lad.dao;

import java.util.List;

import com.lad.bo.MessageBo;

public interface IMessageDao extends IBaseDao {

	public MessageBo insert(MessageBo messageBo);

	public MessageBo selectById(String messageId);
	
	public  List<MessageBo> selectByUserId(String userId);
}
