package com.lad.service;

import com.lad.bo.*;
import com.mongodb.WriteResult;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

public interface IUserService extends IBaseService {

	public UserBo save(UserBo userBo);

	public UserBo updatePassword(UserBo userBo);

	public WriteResult updatePhone(UserBo userBo);

	public WriteResult updateHeadPictureName(UserBo userBo);

	public WriteResult updateUserName(UserBo userBo);

	public WriteResult updatePersonalizedSignature(UserBo userBo);

	public WriteResult updateBirthDay(UserBo userBo);

	public WriteResult updateSex(UserBo userBo);

	public WriteResult updateChatrooms(UserBo userBo);

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

	
	void addUserLevel(String userid , long num, int type, double hours);


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


	/**
	 * 查找新增的用户
	 * @param timestamp
	 * @return
	 */
	List<UserBo> getUserByPhoneAndTime(List<String> phones,Date timestamp);

	/**
	 * 更具关键字查找圈子中的用户
	 * @return
	 */
	List<UserBo> searchCircleUsers(HashSet<String> circleUsers, String keywords);

	/**
	 * 添加访问记录
	 * @param userVisitBo
	 * @return
	 */
	UserVisitBo addUserVisit(UserVisitBo userVisitBo);

	/**
	 * 更新访问时间
	 * @param id
	 * @param date
	 * @return
	 */
	WriteResult updateUserVisit(String id, Date date);

	/**
	 * 我访问的页面记录
	 * @param userid
	 * @param page
	 * @param limit
	 * @return
	 */
	List<UserVisitBo> visitFromMeList(String userid, int type, int page, int limit);

	/**
	 * 访问我的页面记录
	 * @param userid
	 * @param page
	 * @param limit
	 * @return
	 */
	List<UserVisitBo> visitToMeList(String userid, int type,int page, int limit);


	/**
	 * 删除访问记录
	 * @param id
	 * @return
	 */
	WriteResult deleteUserVisit(String id);


	/**
	 * 查找用户访问信息
	 * @param ownerid
	 * @param visitid
	 * @return
	 */
	UserVisitBo findUserVisit(String ownerid, String visitid, int type);

	/**
	 * 修改动态背景图片
	 * @param id
	 * @param pic
	 * @return
	 */
	WriteResult updateUserDynamicPic(String id, String pic);


	/**
	 * 查找用户访问信息
	 * @param ownerid
	 * @return
	 */
	UserVisitBo findUserVisitFirst(String ownerid, int type);

	/**
	 * 查找集合中的用户信息
	 * @param userids
	 * @return
	 */
	List<UserBo> findUserByIds(List<String> userids);


	/**
	 * 修改用户显示的聊天室窗口
	 * @param chatrooms
	 * @return
	 */
	WriteResult updateShowChatrooms(String userid, HashSet<String> chatrooms);

	/**
	 * 修改个人信息
	 * @param userBo
	 * @return
	 */
	WriteResult updateUserInfo(UserBo userBo);

	/**
	 * 第三方登录授权id
	 * @param openid
	 * @return
	 */
	UserBo findByOpenid(String openid);


	/**
	 * 第三方登录授权id
	 * @param openid
	 * @return
	 */
	WriteResult updateRefeshToken(String openid, String acces_token, String refesh_token);

	/**
	 * 根据授权信息，更新用户
	 * @param openid
	 * @return
	 */
	WriteResult updateUserByOpenid(String openid, UserBo userBo);

	/**
	 * 第三方登录授权id
	 * @param openid
	 * @return
	 */
	UserBo findByUnionid(String unionid);

	/**
	 * 最后一次登录记录
	 * @param loginType
	 * @param id
	 * @return
	 */
	WriteResult updateLastLoginTime(int loginType, String id);

	/**
	 * 根据openid更新
	 * @param loginType
	 * @param id
	 * @return
	 */
	WriteResult updateQQUserInfor(String id, String accessToken, String nickname, String userPic, String gender);


	/**
	 * 删除user
	 * @param loginType
	 * @param id
	 * @return
	 */
	WriteResult removeUser(String id);

	public UserBo findUserById(String id);
}
