package com.junlenet.mongodb.demo.service;

import com.junlenet.mongodb.demo.bo.UserBo;

public interface IUserService extends IBaseService {
	
	public UserBo save(UserBo userBo);
	
	public UserBo updatePassword(UserBo userBo);
	
	public UserBo updatePhone(UserBo userBo);
	
	public UserBo updateHeadPictureName(UserBo userBo);

	public UserBo updateUserName(UserBo userBo);
	
	public UserBo updatePersonalizedSignature(UserBo userBo);

	public UserBo updateBirthDay(UserBo userBo);
}
