package com.lad.service.impl;

import com.lad.bo.*;
import com.lad.dao.*;
import com.lad.service.IUserService;
import com.lad.util.Constant;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * 用户service
 * @author huweijun
 */
@Service("userService")
public class UserServiceImpl implements IUserService{

	@Autowired
	private IUserDao userDao;

	@Autowired
	private IRedstarDao redstarDao;

	@Autowired
	private IUserLevelDao userLevelDao;

	@Autowired
	private ICircleTypeDao circleTypeDao;

	@Autowired
	private IUserTasteDao userTasteDao;

	public UserBo save(UserBo userBo){
		userBo = userDao.save(userBo);
		return userBo;
	}

	public UserBo updatePassword(UserBo userBo){
		userBo = userDao.updatePassword(userBo);
		return userBo;
	}

	public WriteResult updatePhone(UserBo userBo){
		return userDao.updatePhone(userBo);
	}

	public WriteResult updateHeadPictureName(UserBo userBo){
		return userDao.updateHeadPictureName(userBo);
	}

	public WriteResult updateUserName(UserBo userBo){
		return userDao.updateUserName(userBo);
	}

	public WriteResult updateBirthDay(UserBo userBo){
		return userDao.updateBirthDay(userBo);
	}

	public WriteResult updateSex(UserBo userBo){
		return userDao.updateSex(userBo);
	}

	public WriteResult updatePersonalizedSignature(UserBo userBo){
		return userDao.updatePersonalizedSignature(userBo);
	}

	public Pager selectPage(UserBo userBo,Pager pager){
		return userDao.selectPage(userBo, pager);
	}

	public WriteResult updateFriends(UserBo userBo) {
		return userDao.updateFriends(userBo);
	}

	public WriteResult updateChatrooms(UserBo userBo) {
		return userDao.updateChatrooms(userBo);
	}

	public UserBo getUser(String userId) {
		return userDao.getUser(userId);
	}

	public List<UserBo> getUserByName(String name) {
		return userDao.getUserByName(name);
	}

	public UserBo getUserByPhone(String phone) {
		return userDao.getUserByPhone(phone);
	}

	public WriteResult updateLocation(String phone, String locationid) {
		return userDao.updateLocation(phone, locationid);
	}


	public List<RedstarBo> findRedUserTotal(String circleId){
		return redstarDao.findRedTotal(circleId);
	}

	public List<RedstarBo> findRedUserWeek(String circleId,int weekNo, int year){
		return redstarDao.findRedWeek(circleId, weekNo, year);
	}

	@Override
	public WriteResult updateTopCircles(String userid, List<String> topCircles) {
		return userDao.updateTopCircles(userid, topCircles);
	}

	@Override
	public UserBo checkByPhone(String phone) {
		return userDao.checkByPhone(phone);
	}

	@Override
	public WriteResult updateUserStatus(String id, int status) {
		return userDao.updateUserStatus(id, status);
	}

	@Override
	public List<CircleTypeBo> selectByLevel(int level) {
		return circleTypeDao.selectByLevel(level, 1);
	}

	@Override
	public List<CircleTypeBo> selectByParent(String name) {
		return circleTypeDao.selectByParent(name, 1);
	}

	@Override
	public CircleTypeBo addCircleType(CircleTypeBo circleTypeBo) {
		return circleTypeDao.insert(circleTypeBo);
	}

	@Override
	public CircleTypeBo findByName(String name, int level) {
		return circleTypeDao.selectByNameLevel(name, level, 1);
	}

	@Override
	@Async
	public void addUserLevel(String userid, long num, int type) {
		UserLevelBo userLevelBo = userLevelDao.findByUserid(userid);
		int level = 1;
		if (userLevelBo == null) {
			userLevelBo = new UserLevelBo();
			userLevelBo.setUserid(userid);
			switch (type){
				case Constant.LEVEL_HOUR:
					userLevelBo.setOnlineHours(num);
				case Constant.LEVEL_PARTY:
					userLevelBo.setLaunchPartys((int) num);
				case Constant.LEVEL_NOTE:
					userLevelBo.setNoteNum((int) num);
				case Constant.LEVEL_COMMENT:
					userLevelBo.setCommentNum((int) num);
				case Constant.LEVEL_TRANS:
					userLevelBo.setTransmitNum((int) num);
				case Constant.LEVEL_SHARE:
					userLevelBo.setShareNum((int) num);
				case Constant.LEVEL_CIRCLE:
					userLevelBo.setCircleNum((int) num);
				default:
					break;
			}
		} else {
			switch (type){
				case Constant.LEVEL_HOUR:
					userLevelBo.setOnlineHours(userLevelBo.getOnlineHours() + num);
				case Constant.LEVEL_PARTY:
					userLevelBo.setLaunchPartys(userLevelBo.getLaunchPartys() + (int)num);
				case Constant.LEVEL_NOTE:
					userLevelBo.setNoteNum(userLevelBo.getNoteNum() + (int) num);
				case Constant.LEVEL_COMMENT:
					userLevelBo.setCommentNum(userLevelBo.getCommentNum () + (int) num);
				case Constant.LEVEL_TRANS:
					userLevelBo.setTransmitNum(userLevelBo.getTransmitNum() + (int) num);
				case Constant.LEVEL_SHARE:
					userLevelBo.setShareNum(userLevelBo.getShareNum() + (int) num);
				case Constant.LEVEL_CIRCLE:
					userLevelBo.setCircleNum(userLevelBo.getShareNum()+ (int) num);
				default:
					break;
			}
			userLevelDao.update(userLevelBo.getId(), num, type);
			level = getLevel(userLevelBo);
		}
		userDao.updateLevel(userLevelBo.getUserid(), level);
	}


	private int getLevel(UserLevelBo userLevelBo){

		long times = userLevelBo.getOnlineHours();

		long hours = times / 3600000;

		int partys = userLevelBo.getLaunchPartys();

		int notes = userLevelBo.getNoteNum();

		int comments = userLevelBo.getCommentNum();

		int trans = userLevelBo.getTransmitNum();

		int shares = userLevelBo.getShareNum();

		int circles = userLevelBo.getCircleNum();


		if (hours >= 300 && partys >= 30 && notes >= 150
				&& comments >= 200 && trans >=200 && shares >= 300 && circles > 1 ) {
			return 6;
		}
		if (hours >= 150 && partys >= 20 && notes >= 100
				&& comments >= 150 && trans >=150 && shares >= 200 && circles > 0  ) {
			return 5;
		}
		if (hours >= 100 && partys >= 10 && notes >= 50
				&& comments >= 80 && trans >=100 && shares >= 150 && circles > 0 ) {
			return 4;
		}
		if (hours >= 60 && partys >= 3 && notes >= 30
				&& comments >= 50 && trans >=60 && shares >= 100 && circles > 0 ) {
			return 3;
		}
		if (hours >= 30 && partys >= 1 && notes >= 10
				&& comments >= 20 && trans >=30 && shares >= 50 ) {
			return 2;
		}
		return 1;
	}

	@Override
	public UserTasteBo addUserTaste(UserTasteBo tasteBo) {
		return userTasteDao.add(tasteBo);
	}

	@Override
	public UserTasteBo findByUserId(String userid) {
		return userTasteDao.findByUserid(userid);
	}

	@Override
	public WriteResult updateUserTaste(String id, LinkedHashSet<String> tastes, int type) {

		switch (type){
			case Constant.ONE:
				return userTasteDao.updateSport(id, tastes);
			case Constant.TWO:
				return userTasteDao.updateMusic(id, tastes);
			case Constant.THREE:
				return userTasteDao.updateLife(id, tastes);
			case Constant.FOUR:
				return userTasteDao.updateTrip(id, tastes);
			default:
				break;
		}
		return null;

	}
}
