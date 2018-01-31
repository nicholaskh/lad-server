package com.lad.dao;

import com.lad.bo.FriendsBo;
import com.mongodb.WriteResult;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

public interface IFriendsDao extends IBaseDao {
	public FriendsBo insert(FriendsBo friendsBo);

	public WriteResult updateBackname(String userid, String friendid, String backname);

	public WriteResult updateTag(String userid, String friendid, List tag);

	public WriteResult updatePhone(String userid, String friendid, HashSet<String> phones);

	public WriteResult updateDescription(String userid, String friendid, String description);

	public WriteResult updateVIP(String userid, String friendid, Integer VIP);

	public WriteResult updateBlack(String userid, String friendid, Integer black);
	
	public FriendsBo getFriendByIdAndVisitorId(String userid, String friendid);
	
	public FriendsBo getFriendByIdAndVisitorIdAgree(String userid, String friendid);
	
	public List<FriendsBo> getFriendByUserid(String userid);
	
	public List<FriendsBo> getFriendByFriendid(String friendid);
	
	public WriteResult delete(String userid, String friendid);
	
	public WriteResult updateApply(String id, int apply, String chatroomid);
	
	public List<FriendsBo> getApplyFriendByuserid(String userid);
	
	public FriendsBo get(String id);

	/**
	 * 更具关键字查找圈子中的用户
	 * @return
	 */
	List<FriendsBo> searchCircleUsers(HashSet<String> circleUsers, String userid,  String keywords);

	/**
	 * 查找不在指定中的用户
	 * @param circleUsers
	 * @param userid
	 * @param keywords
	 * @return
	 */
	List<FriendsBo> searchInviteCircleUsers(HashSet<String> circleUsers, String userid, String keywords);


	/**
	 * 增量查询好友
	 * @param userid
	 * @param timestap
	 * @return
	 */
	List<FriendsBo> getFriendByUserid(String userid, Date timestap);

	/**
	 * 用户名称和头像修改，修改好友信息
	 * @return
	 */
	WriteResult updateUsernameByFriend(String friendid, String username, String userHeadPic);

	/**
	 * 获取指定列表中的好友
	 * @param userid
	 * @param friendids
	 * @return
	 */
	List<FriendsBo> getFriendByInList(String userid, List<String> friendids);

	/**
	 * 更新关联用户状态
	 * @param id
	 * @param relateStatus
	 * @return
	 */
	WriteResult updateRelateStatus(String id, int relateStatus, boolean isParent);

	/**
	 * 查询所有好友申请添加及关联信息
	 * @param userid
	 * @return
	 */
	List<FriendsBo> findAllApplyList(String userid, int page , int limit);
}
