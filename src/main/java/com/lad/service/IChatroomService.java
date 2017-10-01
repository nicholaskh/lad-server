package com.lad.service;

import com.lad.bo.ChatroomBo;
import com.mongodb.WriteResult;

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
}
