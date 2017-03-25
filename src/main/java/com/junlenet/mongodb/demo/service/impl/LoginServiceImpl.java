package com.junlenet.mongodb.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.junlenet.mongodb.demo.bo.UserBo;
import com.junlenet.mongodb.demo.dao.ILoginDao;
import com.junlenet.mongodb.demo.service.ILoginService;

@Service("loginService")
public class LoginServiceImpl implements ILoginService {

	@Autowired
	private ILoginDao loginDao;
	
	public UserBo getUser(String username, String password) {
		UserBo user = loginDao.searchUser(username, password);
		return user;
	}

}
