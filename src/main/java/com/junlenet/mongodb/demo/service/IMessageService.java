package com.junlenet.mongodb.demo.service;

import com.junlenet.mongodb.demo.bo.MessageBo;

public interface IMessageService extends IBaseService {
	
	public MessageBo insert(MessageBo messageBo);
	public MessageBo update_thumbsup_ids(MessageBo messageBo);

}
