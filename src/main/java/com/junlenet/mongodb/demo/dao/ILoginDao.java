package com.junlenet.mongodb.demo.dao;

import com.junlenet.mongodb.demo.bo.UserBo;

public interface ILoginDao extends IBaseDao {
	public UserBo searchUser(String username, String password);
}
