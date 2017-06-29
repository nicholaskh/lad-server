package com.lad.service.impl;

import com.lad.bo.Pager;
import com.lad.bo.RedstarBo;
import com.lad.bo.UserBo;
import com.lad.dao.IRedstarDao;
import com.lad.dao.UserDao;
import com.lad.service.IUserService;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 用户service
 * @author huweijun
 */
@Service("userService")
public class UserServiceImpl implements IUserService{

	@Autowired
	private UserDao userDao;

	@Autowired
	private IRedstarDao redstarDao;

	public UserBo save(UserBo userBo){
		userBo = userDao.save(userBo);
		return userBo;
	}

	public UserBo updatePassword(UserBo userBo){
		userBo = userDao.updatePassword(userBo);
		return userBo;
	}

	public UserBo updatePhone(UserBo userBo){
		userBo = userDao.updatePhone(userBo);
		return userBo;
	}

	public UserBo updateHeadPictureName(UserBo userBo){
		userBo = userDao.updateHeadPictureName(userBo);
		return userBo;
	}

	public UserBo updateUserName(UserBo userBo){
		userBo = userDao.updateUserName(userBo);
		return userBo;
	}

	public UserBo updateBirthDay(UserBo userBo){
		userBo = userDao.updateBirthDay(userBo);
		return userBo;
	}

	public UserBo updateSex(UserBo userBo){
		userBo = userDao.updateSex(userBo);
		return userBo;
	}

	public UserBo updatePersonalizedSignature(UserBo userBo){
		userBo = userDao.updatePersonalizedSignature(userBo);
		return userBo;
	}


	public Set<String> getCollectionNames() {
		return userDao.getCollectionNames();
	}

	public Pager selectPage(UserBo userBo,Pager pager){
		return userDao.selectPage(userBo, pager);
	}

	public UserBo updateFriends(UserBo userBo) {
		return userDao.updateFriends(userBo);
	}

	public UserBo updateChatrooms(UserBo userBo) {
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
		return redstarDao.findRedTotal(circleId, 4);
	}

	public List<RedstarBo> findRedUserWeek(String circleId){
		return redstarDao.findRedWeek(circleId, 4);
	}
}
