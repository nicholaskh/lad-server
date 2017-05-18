package com.lad.service;

import java.util.List;

import com.lad.bo.UserBo;
import com.mongodb.WriteResult;

public interface IUserService extends IBaseService {

	public UserBo save(UserBo userBo);

	public UserBo updatePassword(UserBo userBo);

	public UserBo updatePhone(UserBo userBo);

	public UserBo updateHeadPictureName(UserBo userBo);

	public UserBo updateUserName(UserBo userBo);

	public UserBo updatePersonalizedSignature(UserBo userBo);

	public UserBo updateBirthDay(UserBo userBo);

	public UserBo updateFriends(UserBo userBo);

	public UserBo updateSex(UserBo userBo);

	public UserBo updateChatrooms(UserBo userBo);

	public UserBo getUser(String userId);

	public List<UserBo> getUserByName(String name);

	public UserBo getUserByPhone(String phone);

	public WriteResult updateLocation(String phone, String pointid);

}
