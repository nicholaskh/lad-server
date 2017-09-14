package com.lad.service;

import com.lad.bo.CircleTypeBo;
import com.lad.bo.RedstarBo;
import com.lad.bo.UserBo;
import com.lad.bo.UserTasteBo;
import com.mongodb.WriteResult;
import org.springframework.scheduling.annotation.Async;

import java.util.LinkedHashSet;
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

	/**
	 * 获取等级兴趣分类
	 * @param level
	 * @return
	 */
	List<CircleTypeBo> selectByLevel(int level);

	/**
	 * 获取大分类下所有小分类兴趣
	 * @param name
	 * @return
	 */
	List<CircleTypeBo> selectByParent(String name);

	/**
	 * 添加兴趣分类
	 * @param circleTypeBo
	 * @return
	 */
	CircleTypeBo addCircleType(CircleTypeBo circleTypeBo);

	/**
	 * 根据分类和等级获取指定分类
	 * @param name
	 * @param level
	 * @return
	 */
	CircleTypeBo findByName(String name, int level);

	/**
	 * 用户添加兴趣
	 * @param tasteBo
	 * @return
	 */
	UserTasteBo addUserTaste(UserTasteBo tasteBo);

	/**
	 * 根据用户id查找当前用户的兴趣
	 * @param userid
	 * @return
	 */
	UserTasteBo findByUserId(String userid);

	/**
	 * 用户修改个人兴趣
	 * @param id
	 * @param tastes
	 * @param type
	 * @return
	 */
	WriteResult updateUserTaste(String id, LinkedHashSet<String> tastes, int type);

}
