package com.lad.service.impl;

import com.lad.bo.CollectBo;
import com.lad.dao.CollectDao;
import com.lad.service.ICollectService;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("collectService")
public class CollectServiceImpl implements ICollectService {
	
	@Autowired
	private CollectDao collectDao;
	
	
	@Override
	public CollectBo saveChat(CollectBo chatinfo) {
		return collectDao.save(chatinfo);
	}


	@Override
	public List<CollectBo> findMyChat(String userid) {
		return collectDao.findChatByUserid(userid);
	}


	@Override
	public WriteResult delete(String chatId) {
		return collectDao.delete(chatId);
	}

	@Override
	public List<CollectBo> findChatByUserid(String userid, String start_id, int limit) {
		return collectDao.findChatByUserid(userid, start_id, limit);
	}
}
