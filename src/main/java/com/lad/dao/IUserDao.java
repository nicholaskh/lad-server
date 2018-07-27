package com.lad.dao;

import com.lad.bo.Pager;
import com.lad.bo.UserBo;
import com.mongodb.WriteResult;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * 功能描述：
 * Version: 1.0
 * Time:2017/7/2
 */
public interface IUserDao extends IBaseDao {

    UserBo save(UserBo userBo);

    UserBo updatePassword(UserBo userBo);

    UserBo getUser(String userId);

    List<UserBo> getUserByName(String name);

    UserBo getUserByPhone(String phone);

    WriteResult updatePhone(UserBo userBo);

    WriteResult updateFriends(UserBo userBo);

    WriteResult updateChatrooms(UserBo userBo);

    WriteResult updateHeadPictureName(UserBo userBo);

    WriteResult updateUserName(UserBo userBo);

    WriteResult updateSex(UserBo userBo);

    WriteResult updatePersonalizedSignature(UserBo userBo);

    WriteResult updateBirthDay(UserBo userBo);

    /**
     * 置顶圈子
     * @param userid
     * @param topCircles
     * @return
     */
    WriteResult updateTopCircles(String userid, List<String> topCircles);

    /**
     * 分页查询数据
     * @param userBo
     * @param pager
     * @return
     */
    Pager selectPage(UserBo userBo, Pager pager);

    WriteResult updateLocation(String phone, String locationid);

    List<UserBo> getAllUser();

    /**
     * 更新用户等级
     * @param id
     * @param level
     * @return
     */
    WriteResult updateLevel(String id, int level);

    /**
     * 查找当前手机号是否已注册
     * @param phone
     * @return
     */
    UserBo checkByPhone(String phone);

    /**
     * 查找新增的好友
     * @param timestamp
     * @return
     */
    List<UserBo> getUserByPhoneAndTime(List<String> phones,Date timestamp);

    /**
     * 修改用户状态
     * @param id
     * @param status
     * @return
     */
    WriteResult updateUserStatus(String id, int status);


    /**
     * 更具关键字查找圈子中的用户
     * @return
     */
    List<UserBo> searchCircleUsers(HashSet<String> circleUsers, String keywords);


    /**
     * 修改动态背景图片
     * @param id
     * @param pic
     * @return
     */
    WriteResult updateUserDynamicPic(String id, String pic);

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

	UserBo findUserById(String id);

	WriteResult updateAddress(UserBo userBo);

	List<UserBo> findAllUser();
}
