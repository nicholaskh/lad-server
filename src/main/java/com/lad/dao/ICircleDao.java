package com.lad.dao;

import com.lad.bo.CircleBo;
import com.mongodb.WriteResult;
import org.springframework.data.geo.GeoResults;

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
	 * @param limit
	 * @return
	 */
	List<CircleBo> findMyCircles(String userid, int page, int limit);

	/**
	 * 关键字搜索
	 * @param keyword
	 * @return
	 */
	List<CircleBo> findBykeyword(String keyword,String city, int page, int limit);

	WriteResult updateTotal(String circleid, int total);


	/**
	 * 根据分类查询
	 * @return
	 */
	List<CircleBo> findByType(String tag, String sub_tag, String city, int page,int limit);

	//查询附近的圈子
	GeoResults<CircleBo> findNearCircle(String userid, double[] position, int maxDistance, int limit);

	/**
	 * 查找创建圈子数量
	 * @param createuid
	 * @return
	 */
	long findCreateCricles(String createuid);

	/**
	 * 更新公告
	 * @param circleBo
	 * @return
	 */
	WriteResult updateNotice(CircleBo circleBo);

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
	 * 猜你喜欢圈子，在附近圈子之外
	 * @param userid
	 * @param position
	 * @param minDistance
	 * @return
	 */
	List<CircleBo> selectUsersLike(String userid, String city, double[] position, int minDistance);


	/**
	 * 猜你喜欢圈子,更具好友的兴趣类型经常查询
	 * @param userid
	 * @param tag
	 * @param subTag
	 * @return
	 */
	List<CircleBo> selectUsersLike(String userid, String tag, String subTag);

	/**
	 * 查找圈子，包括已删除的
	 * @param circleid
	 * @return
	 */
	CircleBo selectByIdIgnoreDel(String circleid);


	/**
	 * 查找圈子指定集合里面的圈子
	 * @param circleids
	 * @return
	 */
	List<CircleBo> findCirclesInList(List<String> circleids);


	/**
	 * 根据省市区搜索相关圈子
	 * @param cityName
	 * @param page
	 * @param limit
	 * @return
	 */
	List<CircleBo> findByCityName(String cityName, int page, int limit);

	/**
	 * 根据城市搜索
	 * @param city
	 * @param page
	 * @param limit
	 * @return
	 */
	List<CircleBo> selectByCity(String city, int page, int limit);

}
