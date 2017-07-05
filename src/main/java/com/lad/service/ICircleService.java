package com.lad.service;

import com.lad.bo.CircleBo;
import com.lad.bo.ReasonBo;
import com.mongodb.WriteResult;

import java.util.HashSet;
import java.util.List;

public interface ICircleService extends IBaseService {
	public CircleBo insert(CircleBo circleBo);

	public CircleBo selectById(String circleBoId);

	public List<CircleBo> selectByuserid(String userid);

	public List<CircleBo> selectByType(String tag, String sub_tag,
			String category);

	public WriteResult updateUsers(String circleBoId, HashSet<String> users);

	public WriteResult updateUsersApply(String circleBoId, HashSet<String> usersApply);

	WriteResult updateApplyAgree(String circleBoId, HashSet<String> users, HashSet<String> usersApply);

	public WriteResult updateUsersRefuse(String circleBoId, HashSet<String> usersApply,
			HashSet<String> usersRefuse);

	public WriteResult updateHeadPicture(String circleBoId, String headPicture);

	public WriteResult updateNotes(String circleBoId, HashSet<String> notes);

	/**
	 * 根据创建者查询
	 * @param createid
	 * @return
	 */
	List<CircleBo> findByCreateid(String createid);

	/**
	 * 更新群主
	 * @param circleBo  更新为群主的用户
	 * @return
	 */
	WriteResult updateMaster(CircleBo circleBo);

	/**
	 * 我加入的圈子
	 * @param userid
	 * @param startId
	 * @param gt
	 * @param limit
	 * @return
	 */
	List<CircleBo> findMyCircles(String userid, String startId, boolean gt, int limit);

	/**
	 * 圈子前十
	 * @return
	 */
	List<CircleBo> selectUsersPre(String userid);

	ReasonBo insertApplyReason(ReasonBo reasonBo);

	ReasonBo findByUserAndCircle(String userid, String circleid);

	WriteResult updateApply(String reasonId, int status, String refuse);
}
