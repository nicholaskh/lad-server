package com.lad.service.impl;

import com.lad.bo.ChatroomBo;
import com.lad.bo.ChatroomUserBo;
import com.lad.dao.IChatroomDao;
import com.lad.dao.IChatroomUserDao;
import com.lad.service.IChatroomService;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("chatroomService")
public class ChatroomServiceImpl implements IChatroomService {

	@Autowired
	private IChatroomDao chatroomDao;

	@Autowired
	private IChatroomUserDao chatroomUserDao;
	
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


	@Override
	public ChatroomUserBo insertUser(ChatroomUserBo userBo) {
		return chatroomUserDao.insert(userBo);
	}


	@Override
	public WriteResult deleteUser(String id) {
		return chatroomUserDao.delete(id);
	}

	@Override
	public List<ChatroomUserBo> findByUserRoomid(String chatroomid) {
		return chatroomUserDao.findByRoomid(chatroomid);
	}

	@Override
	public WriteResult updateUserNickname(String id, String nickname) {
		return chatroomUserDao.updateNickname(id, nickname);
	}

	@Override
	public WriteResult updateUserNickname(String userid, String chatroomid, String nickname) {
		return chatroomUserDao.updateNickname(userid, chatroomid, nickname);
	}

	@Override
	public WriteResult deleteChatroomUser(String userid, String chatroomid) {
		return chatroomUserDao.deleteChatroom(userid, chatroomid);
	}

	@Override
	public WriteResult updateDisturb(String id, boolean isDisturb) {
		return chatroomUserDao.updateDisturb(id, isDisturb);
	}

	@Override
	public WriteResult updateShowNick(String id, boolean isShowNick) {
		return chatroomUserDao.updateShowNick(id, isShowNick);
	}

	@Override
	public WriteResult updateShowNick(String userid, String chatroomid, boolean isShowNick) {
		return chatroomUserDao.updateShowNick(userid, chatroomid, isShowNick);
	}

	@Override
	public WriteResult updateDisturb(String userid, String chatroomid, boolean isDisturb) {
		return chatroomUserDao.updateDisturb(userid, chatroomid, isDisturb);
	}

	@Override
	public ChatroomUserBo findChatUserByUserAndRoomid(String userid, String chatroomid) {
		return chatroomUserDao.findByUserAndRoomid(userid, chatroomid);
	}

	@Override
	public WriteResult updateName(String chatRoomId, String name, boolean isNameSet) {
		return chatroomDao.updateName(chatRoomId, name, isNameSet);
	}

	@Override
	public List<ChatroomBo> findMyChatrooms(String userid, Date timestamp) {
		return chatroomDao.findMyChatrooms(userid, timestamp);
	}

	@Override
	public List<ChatroomBo> findMyChatrooms(String userid) {
		return chatroomDao.findMyChatrooms(userid);
	}

	@Override
	public WriteResult deleteTempChat(String targetid, int roomType) {
		return chatroomDao.deleteTempChat(targetid, roomType);
	}

	@Override
	public WriteResult addPartyChartroom(String chatroomId, String partyid) {
		return chatroomDao.addPartyChartroom(chatroomId, partyid);
	}

	@Override
	public WriteResult deleteChatroom(HashSet<String> userids, String chatroomid) {
		return chatroomUserDao.deleteChatroom(userids, chatroomid);
	}

	@Override
	public WriteResult updateNameAndUsers(String chatRoomId, String name, boolean isNameSet, LinkedHashSet<String> users) {
		return chatroomDao.updateNameAndUsers(chatRoomId, name, isNameSet, users);
	}

	@Override
	public List<ChatroomBo> haveSameChatroom(String userid, String friendid) {
		return chatroomDao.haveSameChatroom(userid, friendid);
	}


	@Override
	public WriteResult updateRoomByParams(String chatRoomId, Map<String, Object> params) {
		return chatroomDao.updateRoomByParams(chatRoomId, params);
	}
}
