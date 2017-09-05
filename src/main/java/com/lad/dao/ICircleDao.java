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

	public WriteResult updateNotes(String circleBoId, long noteSize);

	/**
	 * 根据创建者查询
	 * @param createid
	 * @return
	 */
	List<CircleBo> findByCreateid(String createid);

	/**
	 * 更新管理员
	 * @param circleBo  更新管理员
	 * @return
	 */
	WriteResult updateMaster(CircleBo circleBo);

	/**
	 * 更新群主
	 * @param circleBo  更新为群主的用户
	 * @return
	 */
	WriteResult updateCreateUser(CircleBo circleBo);

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

	/**
	 * 关键字搜索
	 * @param keyword
	 * @return
	 */
	List<CircleBo> findBykeyword(String keyword);

	WriteResult updateTotal(String circleid, int total);


	/**
	 * 根据分类查询
	 * @return
	 */
	List<CircleBo> findByType(String type, int level, String startId,  boolean gt,int limit);

	//查询附近的圈子
	List<CircleBo> findNearCircle(double[] position, int maxDistance, int limit);

	/**
	 * 查找创建圈子数量
	 * @param createuid
	 * @return
	 */
	long findCreateCricles(String createuid);

	/**
	 * 更新公告
	 * @param circleid
	 * @param title
	 * @param notice
	 * @return
	 */
	WriteResult updateNotice(String circleid, String title, String notice);

	/**
	 * 更新圈子名称
	 * @param circleid
	 * @param name
	 * @return
	 */
	WriteResult updateCircleName(String circleid, String name);

	/**
	 * 更新圈子开放
	 * @param circleid
	 * @param isOpen
	 * @return
	 */
	WriteResult updateOpen(String circleid, boolean isOpen);

	/**
	 * 更新圈子验证
	 * @param circleid
	 * @param isVerify
	 * @return
	 */
	WriteResult updateisVerify(String circleid, boolean isVerify);


	/**
	 * 更新圈子热度及人员信息
	 * @param circleid
	 * @param num
	 * @param type
	 * @return
	 */
	WriteResult updateCircleHot(String circleid, int num, int type);

}
