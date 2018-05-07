package com.lad.service.impl;

import com.lad.bo.*;
import com.lad.dao.*;
import com.lad.service.ICircleService;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.GeoResults;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

@Service("circleService")
public class CircleServiceImpl implements ICircleService {

	@Autowired
	private ICircleDao circleDao;
	
	@Autowired
	private ICircleHistoryDao circleHistoryDao;

	@Autowired
	private ICircleTypeDao circleTypeDao;

	@Autowired
	private ICircleAddDao circleAddDao;

	@Autowired
	private ICircleNoticeDao circleNoticeDao;

	@Autowired
	private ICircleShowDao circleShowDao;

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
	public List<CircleBo> findMyCircles(String userid, int page, int limit) {
		return circleDao.findMyCircles(userid, page, limit);
	}

	@Override
	public List<CircleBo> selectUsersPre(String userid) {
		return circleDao.selectUsersPre(userid);
	}

	@Override
	public long findCreateCricles(String createuid) {
		return circleDao.findCreateCricles(createuid);
	}

	@Override
	public WriteResult updateCreateUser(CircleBo circleBo) {
		return circleDao.updateCreateUser(circleBo);
	}


	@Override
	public List<CircleBo> findBykeyword(String keyword, String city, int page, int limit) {
		return circleDao.findBykeyword(keyword, city, page, limit);
	}

	@Override
	public GeoResults<CircleBo> findNearCircle(String userid, double[] position, int maxDistance, int limit) {
		return circleDao.findNearCircle(userid, position, maxDistance, limit);
	}

	@Override
	public List<CircleBo> findByType(String tag, String sub_tag ,String city,int page,int limit) {
		return circleDao.findByType(tag, sub_tag, city, page, limit);
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
	public List<CircleTypeBo> selectByLevel(int level, int type) {
		return circleTypeDao.selectByLevel(level, type);
	}

	@Override
	public CircleTypeBo addCircleType(CircleTypeBo circleTypeBo) {
		return circleTypeDao.insert(circleTypeBo);
	}

	@Override
	public List<CircleTypeBo> selectByParent(String name) {
		return circleTypeDao.selectByParent(name, 0);
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
	public CircleTypeBo findByName(String name, int level, int type) {
		return circleTypeDao.selectByNameLevel(name, level, type);
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
	public List<CircleBo> findByCityName(String cityName, int page, int limit) {
		return circleDao.findByCityName(cityName, page, limit);
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

	@Override
	public List<CircleBo> selectUsersLike(String userid, String city, double[] position, int minDistance) {
		return circleDao.selectUsersLike(userid, city, position, minDistance);
	}

	@Override
	public CircleBo selectByIdIgnoreDel(String circleid) {
		return circleDao.selectByIdIgnoreDel(circleid);
	}

	@Override
	public List<CircleBo> findCirclesInList(List<String> circleids) {
		return circleDao.findCirclesInList(circleids);
	}

	@Override
	public CircleHistoryBo findCircleHisById(String id) {
		return circleHistoryDao.findCircleHisById(id);
	}

	@Override
	public List<CircleHistoryBo> findCircleHisByUserid(String userid, int type, int page, int limit) {
		return circleHistoryDao.findCircleHisByUserid(userid, type, page, limit);
	}

	@Override
	public List<CircleHistoryBo> findCircleHisByCricleid(String circleid, int type, int page, int limit) {
		return circleHistoryDao.findCircleHisByCricleid(circleid, type, page, limit);
	}

	@Override
	public WriteResult deleteHis(String id) {
		return circleHistoryDao.deleteHis(id);
	}

	@Override
	public WriteResult deleteHisBitch(List<String> ids) {
		return circleHistoryDao.deleteHisBitch(ids);
	}


	@Override
	public CircleNoticeBo addNotice(CircleNoticeBo noticeBo) {
		return circleNoticeDao.addNotice(noticeBo);
	}

	@Override
	public List<CircleNoticeBo> findCircleNotice(String targetid, int noticeType,int page, int limit) {
		return circleNoticeDao.findCircleNotice(targetid, noticeType, page, limit);
	}

	@Override
	public CircleNoticeBo findLastNotice(String targetid, int noticeType) {
		return circleNoticeDao.findLastNotice(targetid, noticeType);
	}

	@Override
	public WriteResult deleteNotice(String id, String userid) {
		return circleNoticeDao.deleteNotice(id, userid);
	}

	@Override
	public WriteResult updateNoticeRead(String id, LinkedHashSet<String> readUsers, LinkedHashSet<String>
			unReadUsers) {
		return circleNoticeDao.updateNoticeRead(id, readUsers, unReadUsers);
	}

	@Override
	public WriteResult updateNotice(CircleNoticeBo noticeBo) {
		return circleNoticeDao.updateNotice(noticeBo);
	}

	@Override
	public CircleNoticeBo findNoticeById(String id) {
		return circleNoticeDao.findNoticeById(id);
	}

	@Override
	public CircleShowBo addCircleShow(CircleShowBo showBo) {
		return circleShowDao.addCircleShow(showBo);
	}

	@Override
	public List<CircleShowBo> findCircleShows(String circleid, int page, int limit) {
		return circleShowDao.findCircleShows(circleid, page, limit);
	}

	@Override
	public WriteResult deleteShow(String targetid) {
		return circleShowDao.deleteShow(targetid);
	}

	@Override
	public List<CircleNoticeBo> findUnReadNotices(String userid, String targetid, int noticeType) {
		return circleNoticeDao.unReadNotice(userid, targetid, noticeType);
	}

	@Override
	public List<CircleNoticeBo> findUnReadNotices(String userid, String targetid, int noticeType, int page, int limit) {
		return circleNoticeDao.unReadNotice(userid, targetid, noticeType, page, limit);
	}


	@Override
	public List<CircleNoticeBo> findNoticeByIds(String... ids) {
		return circleNoticeDao.findNoticeByIds(ids);
	}

	@Override
	public GeoResults<CircleHistoryBo> findNearPeopleDis(String cirlcid, String userid, double[] position, double
			maxDistance) {
		return circleHistoryDao.findNearPeople(cirlcid, userid, position, maxDistance);
	}

	@Override
	public List<CircleBo> selectByCity(String city, int page, int limit) {
		return circleDao.selectByCity(city, page, limit);
	}

	@Override
	public long findNoticeTotal(String targetid, int noticeType) {
		return circleNoticeDao.findNoticeTotal(targetid, noticeType);
	}

	@Override
	public WriteResult updateTakeShow(String circleid, boolean takeShow) {
		return circleDao.updateTakeShow(circleid, takeShow);
	}

	@Override
	public List<CircleBo> selectByRegexName(String showType) {
		return circleDao.selectByRegexName(showType);
	}

	@Override
	public WriteResult updateCircleTypeTimes(String id) {
		return circleTypeDao.updateCircleTypeTimes(id);
	}
}
