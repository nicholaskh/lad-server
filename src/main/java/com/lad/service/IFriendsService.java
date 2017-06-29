package com.lad.service;

import com.lad.bo.FriendsBo;
import com.mongodb.WriteResult;

import java.util.HashSet;
import java.util.List;

public interface IFriendsService extends IBaseService {
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
	
	public WriteResult updateApply(String id, int apply);
	
	public List<FriendsBo> getApplyFriendByuserid(String userid);
	
	public FriendsBo get(String id);

}
