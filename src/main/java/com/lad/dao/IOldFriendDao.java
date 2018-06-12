package com.lad.dao;

import java.util.List;
import java.util.Map;

import com.lad.bo.OldFriendRequireBo;
import com.lad.bo.UserBo;
import com.mongodb.WriteResult;

public interface IOldFriendDao extends IBaseDao {

	String getInitData(String id);

	String insert(OldFriendRequireBo requireBo);

	long getRequireCount(String uid);

	WriteResult deleteByRequireId(String uid, String requireId);

	OldFriendRequireBo getByRequireId(String id, String requireId);

	WriteResult updateByParams(Map<String,Object> params, String requireId);

	List<UserBo> findListByKeyword(String keyWord, int page, int limit, String uid);

	List<OldFriendRequireBo> findNewPublish(int page, int limit, String id);

	OldFriendRequireBo getByRequireId(String requireId);

	OldFriendRequireBo getRequireByCreateUid(String id);

	List<Map> getRecommend(OldFriendRequireBo require);


}
