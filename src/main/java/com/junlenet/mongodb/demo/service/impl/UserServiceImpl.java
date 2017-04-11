package com.junlenet.mongodb.demo.service.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.junlenet.mongodb.demo.bo.Pager;
import com.junlenet.mongodb.demo.bo.UserBo;
import com.junlenet.mongodb.demo.dao.UserDao;
import com.junlenet.mongodb.demo.service.IRegistService;
import com.junlenet.mongodb.demo.service.IUserService;

/**
 * 用户service
 * @author huweijun
 * @date 2016年7月7日 下午8:48:32
 */
@Service("userService")
public class UserServiceImpl implements IUserService{

		@Autowired
		private UserDao userDao;
	
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
}
