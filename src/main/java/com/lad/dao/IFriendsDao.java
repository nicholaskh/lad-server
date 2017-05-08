package com.lad.dao;

import java.util.List;

import com.lad.bo.FriendsBo;
import com.mongodb.WriteResult;

public interface IFriendsDao extends IBaseDao {
	public FriendsBo insert(FriendsBo friendsBo);

	public WriteResult updateBackName(String userid, String firendid, String backName);

	public WriteResult updateTag(String userid, String firendid, List tag);

	public WriteResult updatePhone(String userid, String firendid, String phone);

	public WriteResult updateDescription(String userid, String firendid, String description);

	public WriteResult updateVIP(String userid, String firendid, Integer VIP);

	public WriteResult updateBlack(String userid, String firendid, Integer black);

}
