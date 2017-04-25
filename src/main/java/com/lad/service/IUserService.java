package com.lad.service;

import com.lad.bo.UserBo;

public interface IUserService extends IBaseService {
	
	public UserBo save(UserBo userBo);
	
	public UserBo updatePassword(UserBo userBo);
	
	public UserBo updatePhone(UserBo userBo);
	
	public UserBo updateHeadPictureName(UserBo userBo);

	public UserBo updateUserName(UserBo userBo);
	
	public UserBo updatePersonalizedSignature(UserBo userBo);

	public UserBo updateBirthDay(UserBo userBo);
	
	public UserBo updateSex(UserBo userBo);
}
