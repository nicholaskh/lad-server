package com.lad.service.impl;

import com.lad.bo.*;
import com.lad.dao.*;
import com.lad.service.ICircleService;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service("circleService")
public class CircleServiceImpl implements ICircleService {

	@Autowired
	private ICircleDao circleDao;

	@Autowired
	private IReasonDao reasonDao;
	
	@Autowired
	private ICircleHistoryDao circleHistoryDao;

	@Autowired
	private ICircleTypeDao circleTypeDao;

	@Autowired
	private ICircleAddDao circleAddDao;

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

	public WriteResult updateUsersApply(String circleBoId, HashSet<String> usersApply) {
		return circleDao.updateUsersApply(circleBoId, usersApply);
	}

	@Override
	public WriteResult updateApplyAgree(String circleBoId, HashSet<String> users, HashSet<String> usersApply) {
		return circleDao.updateApplyAgree(circleBoId, users, usersApply);
	}

	public WriteResult updateUsersRefuse(String circleBoId, HashSet<String> usersApply,
										 HashSet<String> usersRefuse) {
		return circleDao.updateUsersRefuse(circleBoId, usersApply, usersRefuse);
	}

	public WriteResult updateHeadPicture(String circleBoId, String headPicture) {
		return circleDao.updateHeadPicture(circleBoId, headPicture);
	}

	public List<CircleBo> selectByType(String tag, String sub_tag,
			String category) {
		return circleDao.selectByType(tag, sub_tag, category);
	}

	public WriteResult updateNotes(String circleBoId, long noteSize) {
		return circleDao.updateNotes(circleBoId, noteSize);
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
	public List<CircleBo> selectUsersPre(String userid) {
		return circleDao.selectUsersPre(userid);
	}

	public ReasonBo insertApplyReason(ReasonBo reasonBo){
		return reasonDao.insert(reasonBo);
	}

	public ReasonBo findByUserAndCircle(String userid, String circleid){
		return reasonDao.findByUserAndCircle(userid, circleid);
	}

	public WriteResult updateApply(String reasonId, int status, String refuse){
		return reasonDao.updateApply(reasonId, status,refuse);
	}

	@Override
	public long findCreateCricles(String createuid) {
		return 0;
	}

	@Override
	public WriteResult updateCreateUser(CircleBo circleBo) {
		return circleDao.updateCreateUser(circleBo);
	}


	@Override
	public List<CircleBo> findBykeyword(String keyword, int page, int limit) {
		return circleDao.findBykeyword(keyword, page, limit);
	}

	@Override
	public List<CircleBo> findNearCircle(String userid, double[] position, int maxDistance, int limit) {
		return circleDao.findNearCircle(userid, position, maxDistance, limit);
	}

	@Override
	public List<CircleBo> findByType(String tag, String sub_tag ,int page,int limit) {
		return circleDao.findByType(tag, sub_tag, page, limit);
	}

	@Override
	public List<CircleHistoryBo> findNearPeople(String circleid, String userid, double[] position, double
			maxDistance) {
		return circleHistoryDao.findNear(circleid, userid, position, maxDistance);
	}

	@Override
	public CircleHistoryBo insertHistory(CircleHistoryBo circleHistoryBo) {
		return circleHistoryDao.insert(circleHistoryBo);
	}

	@Override
	public WriteResult updateHistory(String id, double[] position) {
		return circleHistoryDao.updateHistory(id, position);
	}

	@Override
	public CircleHistoryBo findByUserIdAndCircleId(String userid, String circleid) {
		return circleHistoryDao.findByUserIdAndCircleId(userid, circleid);
	}


	@Async
	public WriteResult updateTotal(String circleid, int total) {
		return circleDao.updateTotal(circleid, total);
	}

	@Override
	public List<CircleTypeBo> selectByLevel(int level) {
		return circleTypeDao.selectByLevel(level, 0);
	}

	@Override
	public List<CircleTypeBo> selectByParent(String name) {
		return circleTypeDao.selectByParent(name, 0);
	}

	@Override
	public CircleTypeBo addCircleType(CircleTypeBo circleTypeBo) {
		return circleTypeDao.insert(circleTypeBo);
	}

	@Override
	public CircleTypeBo findEsixtTagName(String keyword) {
		return circleTypeDao.findEsixtTagName(keyword, 0);
	}

	@Override
	public List<CircleTypeBo> selectByPage(int start, int limit) {
		return circleTypeDao.findAll(start, limit, 0);
	}

	@Override
	public CircleTypeBo findByName(String name, int level) {
		return circleTypeDao.selectByNameLevel(name, level, 0);
	}

	@Override
	public List<CircleTypeBo> findAllCircleTypes() {
		return circleTypeDao.findAll();
	}


	@Override
	public WriteResult updateOpen(String circleid, boolean isOpen) {
		return circleDao.updateOpen(circleid, isOpen);
	}

	@Override
	public WriteResult updateisVerify(String circleid, boolean isVerify) {
		return circleDao.updateisVerify(circleid, isVerify);
	}

	@Override
	public WriteResult updateNotice(CircleBo circleBo) {
		return circleDao.updateNotice(circleBo);
	}

	@Override
	public WriteResult updateCircleName(String circleid, String name) {
		return circleDao.updateCircleName(circleid, name);
	}

	@Override
	public WriteResult updateCircleHot(String circleid, int num, int type) {
		return circleDao.updateCircleHot(circleid, num, type);
	}

	@Override
	public List<CircleBo> findByCitys(String province, String city, String district, int page, int limit) {
		return circleDao.findByCitys(province, city, district, page, limit);
	}

	@Override
	public List<CircleBo> findRelatedCircles(String circleid, String tag, String sub_tag, int page, int limit) {
		return circleDao.findRelatedCircles(circleid, tag, sub_tag, page, limit);
	}

	@Override
	public CircleBo findByTagAndName(String name, String tag, String sub_tag) {
		return circleDao.findByTagAndName(name, tag, sub_tag);
	}

	@Override
	public CircleAddBo insertCircleAdd(CircleAddBo addBo) {
		return circleAddDao.insert(addBo);
	}

	@Override
	public CircleAddBo findHisByUserAndCircle(String userid, String circleid) {
		return circleAddDao.findByUserAndCircle(userid, circleid);
	}

	@Override
	public WriteResult updateJoinStatus(String id, int status) {
		return circleAddDao.updateJoinStatus(id, status);
	}
}
