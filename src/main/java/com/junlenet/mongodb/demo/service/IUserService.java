package com.junlenet.mongodb.demo.service;

import com.junlenet.mongodb.demo.bo.UserBo;

public interface IUserService extends IBaseService {
	
	public UserBo save(UserBo userBo);
	
	public UserBo updatePassword(UserBo userBo);
}
