package com.lad.service;

import com.lad.bo.*;
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

	public WriteResult updateNotes(String circleBoId, long noteSize);

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


	WriteResult updateCreateUser(CircleBo circleBo);

	/**
	 * 关键字搜索
	 * @param keyword
	 * @return
	 */
	List<CircleBo> findBykeyword(String keyword);


	/**
	 * 查询附近的圈子
	 */
	List<CircleBo> findNearCircle(String userid, double[] position, int maxDistance, int limit);

	/**
	 * 根据分类查询
	 * @return
	 */
	List<CircleBo> findByType(String tag, String sub_tag ,int page,int limit);

	
	List<CircleHistoryBo> findNearPeople(String cirlcid, String userid, double[] position, double maxDistance);


	CircleHistoryBo insertHistory(CircleHistoryBo circleHistoryBo);


	WriteResult updateHistory(String id, double[] position);

	
	CircleHistoryBo findByUserIdAndCircleId(String userid, String circleid);

	/**
	 * 更新圈子人气
	 * @param circleid
	 * @param total
	 * @return
	 */
	WriteResult updateTotal(String circleid, int total);

	List<CircleTypeBo> selectByLevel(int level);

	List<CircleTypeBo> selectByParent(String name);

	CircleTypeBo addCircleType(CircleTypeBo circleTypeBo);

	/**
	 * 查找圈子分类是不是存在相同名称
	 * @param keyword
	 * @return
	 */
	CircleTypeBo findEsixtTagName(String keyword);

	List<CircleTypeBo> selectByPage(int start, int limit);

	CircleTypeBo findByName(String name, int level);

	/**
	 * 查找所有圈子类型
	 * @return
	 */
	List<CircleTypeBo> findAllCircleTypes();

	long findCreateCricles(String createuid);

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
	 * 更新圈子热度及人员信息
	 * @param circleid
	 * @param num
	 * @param type
	 * @return
	 */
	WriteResult updateCircleHot(String circleid, int num, int type);

	/**
	 * 根据城市，搜索城市下相关圈子
	 * @param province
	 * @param city
	 * @param district
	 * @param page
	 * @param limit
	 * @return
	 */
	List<CircleBo> findByCitys(String province, String city, String district, int page, int limit);

	/**
	 * 根据圈子类型查找相关圈子
	 * @param tag
	 * @param sub_tag
	 * @param page
	 * @param limit
	 * @return
	 */
	List<CircleBo> findRelatedCircles(String circleid, String tag, String sub_tag, int page, int limit);

	/**
	 * 查找圈子类型下是否有相同名称的圈子
	 * @param tag
	 * @param sub_tag
	 * @return
	 */
	CircleBo findByTagAndName(String name, String tag, String sub_tag);


	/**
	 * 添加用户加入圈子记录
	 * @param addBo
	 * @return
	 */
	CircleAddBo insertCircleAdd(CircleAddBo addBo);

	/**
	 *根据用户和圈子查找用户添加记录g
	 * @param userid
	 * @param circleid
	 * @return
	 */
	CircleAddBo findHisByUserAndCircle(String userid, String circleid);

	/**
	 * 修改用户加入圈子记录
	 * @param id
	 * @param status
	 * @return
	 */
	WriteResult updateJoinStatus(String id, int status);

}
