package com.lad.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lad.bo.FriendsBo;
import com.lad.dao.IFriendsDao;
import com.lad.service.IFriendsService;
import com.mongodb.WriteResult;

@Service("friendsService")
public class FriendsServiceImpl implements IFriendsService {

	@Autowired
	private IFriendsDao friendsDao;
	
	public FriendsBo insert(FriendsBo friendsBo) {
		return friendsDao.insert(friendsBo);
	}

	public WriteResult updateBackName(String userid, String firendid, String backName) {
		return friendsDao.updateBackName(userid, firendid, backName);
	}

	public WriteResult updateTag(String userid, String firendid, List tag) {
		return friendsDao.updateTag(userid, firendid, tag);
	}

	public WriteResult updatePhone(String userid, String firendid, String phone) {
		return friendsDao.updatePhone(userid, firendid, phone);
	}

	public WriteResult updateDescription(String userid, String firendid, String description) {
		return friendsDao.updateDescription(userid, firendid, description);
	}

	public WriteResult updateVIP(String userid, String firendid, Integer VIP) {
		return friendsDao.updateVIP(userid, firendid, VIP);
	}

	public WriteResult updateBlack(String userid, String firendid, Integer black) {
		return friendsDao.updateBlack(userid, firendid, black);
	}

	public FriendsBo getFriendByIdAndVisitorId(String userid, String firendid) {
		return friendsDao.getFriendByIdAndVisitorId(userid, firendid);
	}

}
