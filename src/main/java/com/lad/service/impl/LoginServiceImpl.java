package com.lad.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lad.bo.UserBo;
import com.lad.dao.ILoginDao;
import com.lad.service.ILoginService;

@Service("loginService")
public class LoginServiceImpl implements ILoginService {

	@Autowired
	private ILoginDao loginDao;
	
	public UserBo getUser(String username, String password) {
		UserBo user = loginDao.searchUser(username, password);
		return user;
	}

}
