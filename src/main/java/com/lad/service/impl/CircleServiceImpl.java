package com.lad.service.impl;

import com.lad.bo.CircleBo;
import com.lad.dao.ICircleDao;
import com.lad.service.ICircleService;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service("circleService")
public class CircleServiceImpl implements ICircleService {

	@Autowired
	private ICircleDao circleDao;

	public CircleBo insert(CircleBo circleBo) {
		return circleDao.insert(circleBo);
	}

	public CircleBo selectById(String circleBoId) {
		return circleDao.selectById(circleBoId);
	}

	public List<CircleBo> selectByuserid(String userid) {
		return circleDao.selectByuserid(userid);
	}

	public WriteResult updateUsers(String circleBoId, HashSet<String> users) {
		return circleDao.updateUsers(circleBoId, users);
	}

	public WriteResult updateUsersApply(String circleBoId,
			HashSet<String> usersApply) {
		return circleDao.updateUsersApply(circleBoId, usersApply);
	}

	public WriteResult updateUsersRefuse(String circleBoId,
			HashSet<String> usersRefuse) {
		return circleDao.updateUsersRefuse(circleBoId, usersRefuse);
	}

	public WriteResult updateHeadPicture(String circleBoId, String headPicture) {
		return circleDao.updateHeadPicture(circleBoId, headPicture);
	}

	public List<CircleBo> selectByType(String tag, String sub_tag,
			String category) {
		return circleDao.selectByType(tag, sub_tag, category);
	}

	public WriteResult updateNotes(String circleBoId, HashSet<String> notes) {
		return circleDao.updateNotes(circleBoId, notes);
	}

	@Override
	public List<CircleBo> findByCreateid(String createid) {
		return circleDao.findByCreateid(createid);
	}

	@Override
	public WriteResult updateMaster(CircleBo circleBo) {
		return circleDao.updateMaster(circleBo);
	}

	@Override
	public List<CircleBo> findMyCircles(String userid, String startId, boolean gt, int limit) {
		return circleDao.findMyCircles(userid,startId,gt, limit);
	}

	@Override
	public List<CircleBo> selectUsersPre() {
		return circleDao.selectUsersPre();
	}
}
