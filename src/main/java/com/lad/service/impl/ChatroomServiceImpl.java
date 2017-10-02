package com.lad.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lad.bo.ChatroomBo;
import com.lad.dao.IChatroomDao;
import com.lad.service.IChatroomService;
import com.mongodb.WriteResult;

import java.util.LinkedHashSet;

@Service("chatroomService")
public class ChatroomServiceImpl implements IChatroomService {

	@Autowired
	private IChatroomDao chatroomDao;
	
	public ChatroomBo insert(ChatroomBo chatroom) {
		return chatroomDao.insert(chatroom);
	}

	public ChatroomBo updateName(ChatroomBo chatroom) {
		return chatroomDao.updateName(chatroom);
	}

	public ChatroomBo get(String chatroomId) {
		return chatroomDao.get(chatroomId);
	}

	public ChatroomBo updateUsers(ChatroomBo chatroom) {
		return chatroomDao.updateUsers(chatroom);
	}

	public WriteResult delete(String chatroomId) {
		return chatroomDao.delete(chatroomId);
	}

	public ChatroomBo selectByUserIdAndFriendid(String userid, String friendid) {
		return chatroomDao.selectByUserIdAndFriendid(userid, friendid);
	}

	public ChatroomBo selectBySeq(int seq) {
		return chatroomDao.selectBySeq(seq);
	}

	public WriteResult setSeqExpire(int seq) {
		return chatroomDao.setSeqExpire(seq);
	}

	@Override
	public boolean withInRange(String chatroomId, double[] position, int radius) {
		return chatroomDao.withInRange(chatroomId, position, radius);
	}

	@Override
	public ChatroomBo selectBySeqInTen(int seq, double[] position, int radius) {
		return chatroomDao.selectBySeqInTen(seq, position, radius);
	}

	@Override
	public WriteResult updateMaster(String chatroomId, String masterid) {
		return chatroomDao.updateMaster(chatroomId, masterid);
	}

	@Override
	public WriteResult updateName(String chatroomId, String name) {
		return chatroomDao.updateName(chatroomId, name);
	}

	@Override
	public WriteResult updateDescription(String chatroomId, String description) {
		return chatroomDao.updateDescription(chatroomId, description);
	}

	@Override
	public WriteResult updateUsers(String chatroomId, LinkedHashSet<String> users) {
		return chatroomDao.updateUsers(chatroomId, users);
	}

	@Override
	public WriteResult updateOpen(String chatroomId, boolean isOpen) {
		return chatroomDao.updateOpen(chatroomId, isOpen);
	}

	@Override
	public WriteResult updateVerify(String chatroomId, boolean isVerify) {
		return chatroomDao.updateVerify(chatroomId, isVerify);
	}

	@Override
	public WriteResult remove(String chatroomId) {
		return chatroomDao.remove(chatroomId);
	}
}
