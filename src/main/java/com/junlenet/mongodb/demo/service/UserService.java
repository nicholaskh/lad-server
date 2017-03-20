package com.junlenet.mongodb.demo.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.junlenet.mongodb.demo.bo.Pager;
import com.junlenet.mongodb.demo.bo.UserBo;
import com.junlenet.mongodb.demo.dao.UserDao;

/**
 * 用户service
 * @author huweijun
 * @date 2016年7月7日 下午8:48:32
 */
@Service
public class UserService {

		@Autowired
		private UserDao userDao;
	
		public UserBo save(UserBo userBo){
			userBo = userDao.save(userBo);
			return userBo;
		}
	
		public Set<String> getCollectionNames() {
			return userDao.getCollectionNames();
		}
		
		public Pager selectPage(UserBo userBo,Pager pager){
			return userDao.selectPage(userBo, pager);
		}
}
