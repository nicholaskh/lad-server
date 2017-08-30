package com.lad.service;

import com.lad.bo.RedstarBo;
import com.lad.bo.UserBo;
import com.mongodb.WriteResult;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

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

	public WriteResult updateLocation(String phone, String locationid);

	/**
	 * 红人总榜
	 * @param circleId
	 * @return
	 */
	List<RedstarBo> findRedUserTotal(String circleId);

	/**
	 * 红人周榜
	 * @param circleid
	 * @param weekNo
	 * @param year
	 * @return
	 */
	List<RedstarBo> findRedUserWeek(String circleid, int weekNo, int year);

	/**
	 * 置顶圈子
	 * @param userid
	 * @param topCircles
	 * @return
	 */
	WriteResult updateTopCircles(String userid, List<String> topCircles);

	@Async
	void addUserLevel(String userid , long num, int type);


	/**
	 * 查找当前手机号是否已注册
	 * @param phone
	 * @return
	 */
	UserBo checkByPhone(String phone);

	/**
	 * 修改用户状态
	 * @param id
	 * @param status
	 * @return
	 */
	WriteResult updateUserStatus(String id, int status);

}
