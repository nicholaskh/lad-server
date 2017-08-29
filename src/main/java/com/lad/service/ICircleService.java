package com.lad.service;

import com.lad.bo.CircleBo;
import com.lad.bo.CircleHistoryBo;
import com.lad.bo.CircleTypeBo;
import com.lad.bo.ReasonBo;
import com.mongodb.WriteResult;
import org.springframework.scheduling.annotation.Async;

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
	List<CircleBo> findNearCircle(double[] position, int maxDistance, int limit);

	/**
	 * 根据分类查询
	 * @return
	 */
	List<CircleBo> findByType(String type, int level, String startId,  boolean gt,int limit);

	
	List<CircleHistoryBo> findNearPeople(String cirlcid, String userid, double[] position, double maxDistance);


	CircleHistoryBo insertHistory(CircleHistoryBo circleHistoryBo);


	WriteResult updateHistory(String id, double[] position);

	
	CircleHistoryBo findByUserIdAndCircleId(String userid, String circleid);

	@Async
	WriteResult updateTotal(String circleid, int total);

	List<CircleTypeBo> selectByLevel(int level);

	List<CircleTypeBo> selectByParent(String name);

	CircleTypeBo addCircleType(CircleTypeBo circleTypeBo);

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

}
