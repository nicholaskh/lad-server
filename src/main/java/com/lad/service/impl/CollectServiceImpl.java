package com.lad.service.impl;

import com.lad.bo.CollectBo;
import com.lad.dao.CollectDao;
import com.lad.service.ICollectService;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;

@Service("collectService")
public class CollectServiceImpl implements ICollectService {
	
	@Autowired
	private CollectDao collectDao;
	
	
	@Override
	public CollectBo insert(CollectBo chatinfo) {
		return collectDao.insert(chatinfo);
	}

	@Override
	public WriteResult delete(String chatId) {
		return collectDao.delete(chatId);
	}

	@Override
	public List<CollectBo> findChatByUserid(String userid, String start_id, int limit, int type) {
		return collectDao.findChatByUserid(userid, start_id, limit, type);
	}

	@Override
	public CollectBo findById(String id) {
		return collectDao.findById(id);
	}

	@Override
	public List<CollectBo> findByUseridAndType(String userid, int page, int limit, int type) {
		return collectDao.findAllByUseridType(userid, page, limit, type);
	}

	@Override
	public List<CollectBo> findAllByUserid(String userid, int page, int limit) {
		return collectDao.findAllByUserid(userid, page, limit);
	}

	@Override
	public WriteResult updateTags(String id, LinkedHashSet<String> userTages) {
		return collectDao.updateTags(id, userTages);
	}

	@Override
	public List<CollectBo> findByKeyword(String keyword) {
		return collectDao.findByKeyword(keyword);
	}

	@Override
	public List<CollectBo> findByTag(String tag) {
		return collectDao.findByTag(tag);
	}
}
