package com.lad.service;

import java.util.HashSet;

import com.lad.bo.CircleBo;

public interface ICircleService extends IBaseService {
	public CircleBo insert(CircleBo circleBo);

	public CircleBo selectById(String circleBoId);

	public CircleBo selectByuserid(String userid);

	public CircleBo updateUsers(String circleBoId, HashSet<String> users);

	public CircleBo updateUsersApply(String circleBoId,
			HashSet<String> usersApply);

	public CircleBo updateUsersRefuse(String circleBoId,
			HashSet<String> usersRefuse);

	public CircleBo updateOrganizations(String circleBoId,
			HashSet<String> organizations);

}
