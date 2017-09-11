package com.lad.service.impl;

import com.lad.bo.CollectBo;
import com.lad.dao.CollectDao;
import com.lad.service.ICollectService;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("collecService")
public class CollectServiceImpl implements ICollectService {
	
	@Autowired
	private CollectDao collectionDao;
	
	
	@Override
	public CollectBo saveChat(CollectBo chatinfo) {
		return collectionDao.save(chatinfo);
	}


	@Override
	public List<CollectBo> findMyChat(String userid) {
		return collectionDao.findChatByUserid(userid);
	}


	@Override
	public WriteResult delete(String chatId) {
		return collectionDao.delete(chatId);
	}

	@Override
	public List<CollectBo> findChatByUserid(String userid, String start_id, int limit) {
		return collectionDao.findChatByUserid(userid, start_id, limit);
	}
}
