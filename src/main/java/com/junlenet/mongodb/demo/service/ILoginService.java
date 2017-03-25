package com.junlenet.mongodb.demo.service;

import com.junlenet.mongodb.demo.bo.UserBo;

public interface ILoginService extends IBaseService {
	public UserBo getUser(String username, String password);
}
