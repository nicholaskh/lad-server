package com.lad.dao;

import com.lad.bo.CircleBo;
import com.mongodb.WriteResult;

import java.util.HashSet;
import java.util.List;

public interface ICircleDao extends IBaseDao {

	public CircleBo insert(CircleBo circleBo);

	public CircleBo selectById(String circleBoId);

	public List<CircleBo> selectByuserid(String userid);

	public List<CircleBo> selectByType(String tag, String sub_tag,
			String category);

	WriteResult updateUsers(String circleBoId, HashSet<String> users);

	WriteResult updateUsersApply(String circleBoId,
			HashSet<String> usersApply);

	WriteResult updateApplyAgree(String circleBoId, HashSet<String> users,
								 HashSet<String> usersApply);

	WriteResult updateUsersRefuse(String circleBoId,HashSet<String> usersApply,
			HashSet<String> usersRefuse);

	public WriteResult updateHeadPicture(String circleBoId, String headPicture);

	public WriteResult updateNotes(String circleBoId, HashSet<String> notes);

	WriteResult uddateName(String userid, String name);

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
	 * 返回用户数最多前十圈子
	 * @return
	 */
	List<CircleBo> selectUsersPre(String userid);

	/**
	 * 我的圈子
	 * @param userid
	 * @param startId
	 * @param gt
	 * @param limit
	 * @return
	 */
	List<CircleBo> findMyCircles(String userid, String startId, boolean gt, int limit);

}
