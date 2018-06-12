package com.lad.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lad.bo.OldFriendRequireBo;
import com.lad.bo.UserBo;
import com.lad.dao.IOldFriendDao;
import com.lad.service.IOldFriendService;
import com.mongodb.WriteResult;

@Service("oldFriendService")
public class OldFriendServiceImpl implements IOldFriendService {

	@Autowired
	private IOldFriendDao oldFriendDao;

	@Override
	public String getInitData(String id) {
		return oldFriendDao.getInitData(id);
	}

	@Override
	public String insert(OldFriendRequireBo requireBo) {
		return oldFriendDao.insert(requireBo);
	}

	@Override
	public long getRequireCount(String uid) {
		return oldFriendDao.getRequireCount(uid);
	}

	@Override
	public WriteResult deleteByRequireId(String uid, String requireId) {
		return oldFriendDao.deleteByRequireId(uid,requireId);
	}

	@Override
	public OldFriendRequireBo getByRequireId(String id, String requireId) {
		return oldFriendDao.getByRequireId(id,requireId);
	}

	@Override
	public WriteResult updateByParams(Map<String,Object> params, String requireId) {
		return oldFriendDao.updateByParams(params,requireId);
	}

	@Override
	public List<UserBo> findListByKeyword(String keyWord, int page, int limit, String uid) {
		return oldFriendDao.findListByKeyword(keyWord,page,limit,uid);
	}

	@Override
	public List<OldFriendRequireBo> findNewPublish(int page, int limit, String id) {
		return oldFriendDao.findNewPublish(page,limit,id);
	}

	@Override
	public OldFriendRequireBo getByRequireId(String requireId) {
		return oldFriendDao.getByRequireId(requireId);
	}

	@Override
	public OldFriendRequireBo getRequireByCreateUid(String id) {
		return oldFriendDao.getRequireByCreateUid(id);
	}

	@Override
	public List<Map> getRecommend(OldFriendRequireBo require) {
		return oldFriendDao.getRecommend(require);
	}

	@Override
	public int findPublishNum(String uid) {
		return oldFriendDao.findPublishNum(uid);
	}
	

}
