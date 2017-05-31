package com.lad.service;

import java.util.HashSet;
import java.util.List;

import com.lad.bo.CircleBo;
import com.mongodb.WriteResult;

public interface ICircleService extends IBaseService {
	public CircleBo insert(CircleBo circleBo);

	public CircleBo selectById(String circleBoId);

	public List<CircleBo> selectByuserid(String userid);

	public WriteResult updateUsers(String circleBoId, HashSet<String> users);

	public WriteResult updateUsersApply(String circleBoId,
			HashSet<String> usersApply);

	public WriteResult updateUsersRefuse(String circleBoId,
			HashSet<String> usersRefuse);

	public WriteResult updateOrganizations(String circleBoId,
			HashSet<String> organizations);

	public WriteResult updateHeadPicture(String circleBoId, String headPicture);

}
