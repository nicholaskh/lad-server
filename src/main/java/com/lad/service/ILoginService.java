package com.lad.service;

import com.lad.bo.UserBo;

public interface ILoginService extends IBaseService {
	public UserBo getUser(String username, String password);
}
