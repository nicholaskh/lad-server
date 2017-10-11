package com.lad.service;

import com.lad.bo.ChatroomBo;
import com.lad.bo.ChatroomUserBo;
import com.mongodb.WriteResult;

import java.util.HashMap;
import java.util.LinkedHashSet;

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
	ChatroomUserBo findByUserRoomid(String chatroomid);

	/**
	 * 更新成员昵称
	 * @param id
	 * @return
	 */
	WriteResult updateUserNickname(String id, HashMap<String, String> nicknames);

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
	WriteResult deleteChatroomUser(String chatroomid);
}
