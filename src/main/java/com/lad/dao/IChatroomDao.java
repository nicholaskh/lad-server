package com.lad.dao;

import com.lad.bo.ChatroomBo;
import com.mongodb.WriteResult;

public interface IChatroomDao extends IBaseDao {

	public ChatroomBo insert(ChatroomBo chatroom);

	public ChatroomBo updateName(ChatroomBo chatroom);
	
	public ChatroomBo updateUsers(ChatroomBo chatroom);

	public ChatroomBo get(String chatroomId);

	public WriteResult delete(String chatroomId);
	
	public ChatroomBo selectByUserIdAndFriendid(String userid, String friendid);
	
	public ChatroomBo selectBySeq(int seq);
	
	public WriteResult setSeqExpire(int seq);

	/**
	 * 判断聊天室位置是否在制定的距离内
	 * @param chatroomId  聊天
	 * @param position  点
	 * @param radius  距离
	 * @return  true 在范围； false 不在范围
	 */
	boolean withInRange(String chatroomId, double[] position, int radius);

	/**
	 * 查找到指定范围内，指定序列，距当前时间10分钟内创建的chatroom
	 * @param seq
	 * @param position
	 * @param radius
	 * @return
	 */
	ChatroomBo selectBySeqInTen(int seq, double[] position,int radius);
}
