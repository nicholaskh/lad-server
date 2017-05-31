package com.lad.service.impl;

import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lad.bo.CircleBo;
import com.lad.dao.ICircleDao;
import com.lad.service.ICircleService;
import com.mongodb.WriteResult;

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

	public WriteResult updateOrganizations(String circleBoId,
			HashSet<String> organizations) {
		return circleDao.updateOrganizations(circleBoId, organizations);
	}

}