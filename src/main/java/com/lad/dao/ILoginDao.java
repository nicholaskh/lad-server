package com.lad.dao;

import com.lad.bo.UserBo;

public interface ILoginDao extends IBaseDao {
	public UserBo searchUser(String username, String password);
}
