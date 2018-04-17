package com.lad.service;

import com.lad.bo.ChatroomBo;
import com.lad.bo.ChatroomUserBo;
import com.mongodb.WriteResult;

import java.util.*;

public interface IChatroomService extends IBaseService {
	
	public ChatroomBo insert(ChatroomBo chatroom);

	public ChatroomBo updateName(ChatroomBo chatroom);
	
	public ChatroomBo updateUsers(ChatroomBo chatroom);

	public ChatroomBo get(String chatroomId);
	
	public WriteResult delete(String chatroomId);
	
	public ChatroomBo selectByUserIdAndFriendid(String userid, String friendid);
	
	public ChatroomBo selectBySeq(int seq);
	
	public WriteResult setSeqExpire(int seq);

	/**
	 * 从数据库删除聊天
	 * @param chatroomId
	 * @return
	 */
	WriteResult remove(String chatroomId);

	boolean withInRange(String chatroomId, double[] position, int radius);

	/**
	 * 查找到指定范围内，指定序列，距当前时间10分钟内创建的chatroom
	 * @param seq
	 * @param position
	 * @param radius
	 * @return
	 */
	ChatroomBo selectBySeqInTen(int seq, double[] position,int radius);

	/**
	 * 群主转让
	 * @param chatroomId
	 * @param masterid
	 * @return
	 */
	WriteResult updateMaster(String chatroomId, String masterid);

	/**
	 * 修改群昵称
	 * @param chatroomId
	 * @param name
	 * @return
	 */
	WriteResult updateName(String chatroomId, String name);

	/**
	 * 修改群聊公告
	 * @param chatroomId
	 * @param description
	 * @return
	 */
	WriteResult updateDescription(String chatroomId, String description);

	/**
	 * 修改群成员
	 * @param chatroomId
	 * @param users
	 * @return
	 */
	WriteResult updateUsers(String chatroomId, LinkedHashSet<String> users);

	/**
	 * 修改群成员
	 * @param chatroomId
	 * @param isOpen
	 * @return
	 */
	WriteResult updateOpen(String chatroomId, boolean isOpen);

	/**
	 * 修改群成员
	 * @param chatroomId
	 * @param isVerify
	 * @return
	 */
	WriteResult updateVerify(String chatroomId, boolean isVerify);

	/**
	 * 插入群成员信息
	 * @param userBo
	 * @return
	 */
	ChatroomUserBo insertUser(ChatroomUserBo userBo);

	/**
	 * 更具聊天室查找聊天成员信息
	 * @param chatroomid
	 * @return
	 */
	List<ChatroomUserBo> findByUserRoomid(String chatroomid);

	/**
	 * 更新成员昵称
	 * @param id
	 * @return
	 */
	WriteResult updateUserNickname(String id, String nickname);

	/**
	 * 
	 * @param userid
	 * @param chatroomid
	 * @param nickname
	 * @return
	 */
	WriteResult updateUserNickname(String userid, String chatroomid, String nickname);

	/**
	 *
	 * @param id
	 * @return
	 */
	WriteResult deleteUser(String id);

	/**
	 *
	 * @param chatroomid
	 * @return
	 */
	WriteResult deleteChatroomUser(String userid, String chatroomid);


	/**
	 *
	 * @param id
	 * @return
	 */
	WriteResult updateDisturb(String id, boolean isDisturb);

	/**
	 *
	 * @param id
	 * @return
	 */
	WriteResult updateShowNick(String id, boolean isShowNick);

	/**
	 *
	 * @param userid
	 * @param chatroomid
	 * @param isShowNick
	 * @return
	 */
	WriteResult updateShowNick(String userid, String chatroomid, boolean isShowNick);

	/**
	 *
	 * @param userid
	 * @param chatroomid
	 * @param isDisturb
	 * @return
	 */
	WriteResult updateDisturb(String userid, String chatroomid, boolean isDisturb);

	/**
	 * 查找id里面所有信息
	 * @param chatroomid
	 * @return
	 */
	ChatroomUserBo findChatUserByUserAndRoomid(String userid, String chatroomid);

	/**
	 * 更新群名称
	 * @param chatRoomId
	 * @param name
	 * @param isNameSet
	 * @return
	 */
	WriteResult updateName(String chatRoomId, String name, boolean isNameSet);


	/**
	 * 查找我的聊天列表
	 * @param userid
	 * @param timestamp   最大时间戳
	 * @return
	 */
	List<ChatroomBo> findMyChatrooms(String userid, Date timestamp);

	/**
	 * 查找我的聊天列表
	 * @param userid
	 * @return
	 */
	List<ChatroomBo> findMyChatrooms(String userid);


	/**
	 * 删除临时聊天
	 * @param targetid
	 * @param roomType
	 * @return
	 */
	WriteResult deleteTempChat(String targetid, int roomType);

	/**
	 * 修改群成员
	 * @param chatroomId
	 * @param partyid
	 * @return
	 */
	WriteResult addPartyChartroom(String chatroomId, String partyid);

	/**
	 *
	 * @param chatroomid
	 * @return
	 */
	WriteResult deleteChatroom(HashSet<String> userids, String chatroomid);

	/**
	 * 更新群名称和群用户
	 * @param chatRoomId
	 * @param name
	 * @param isNameSet
	 * @return
	 */
	WriteResult updateNameAndUsers(String chatRoomId, String name, boolean isNameSet, LinkedHashSet<String> users);


	/**
	 * 查询与好友具有相同的群聊
	 * @param userid
	 * @param friendid
	 * @return
	 */
	List<ChatroomBo> haveSameChatroom(String userid, String friendid);


	/**
	 * 更新聊天室参数
	 * @param chatRoomId
	 * @param params
	 * @return
	 */
	WriteResult updateRoomByParams(String chatRoomId, Map<String, Object> params);
}
