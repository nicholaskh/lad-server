package com.lad.service;

import com.lad.bo.*;
import com.mongodb.WriteResult;
import org.springframework.data.geo.GeoResults;

import java.util.HashSet;
import java.util.LinkedHashSet;
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
	 * @param limit
	 * @return
	 */
	List<CircleBo> findMyCircles(String userid, int page, int limit);

	/**
	 * 圈子前十
	 * @return
	 */
	List<CircleBo> selectUsersPre(String userid);


	WriteResult updateCreateUser(CircleBo circleBo);

	/**
	 * 关键字搜索
	 * @param keyword
	 * @return
	 */
	List<CircleBo> findBykeyword(String keyword, int page, int limit);


	/**
	 * 查询附近的圈子
	 */
	GeoResults<CircleBo> findNearCircle(String userid, double[] position, int maxDistance, int limit);

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
	 * 根据城市，搜索城市下相关圈子
	 * @param cityName
	 * @param page
	 * @param limit
	 * @return
	 */
	List<CircleBo> findByCityName(String cityName, int page, int limit);

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


	/**
	 * 猜你喜欢圈子，在附近圈子之外
	 * @param userid
	 * @param position
	 * @param minDistance
	 * @return
	 */
	List<CircleBo> selectUsersLike(String userid, double[] position, int minDistance);

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
	 * 根据id查找
	 * @param id
	 * @return
	 */
	CircleHistoryBo findCircleHisById(String id);

	/**
	 * 根据用户查找
	 * @param userid
	 * @param type
	 * @param page
	 * @param limit
	 * @return
	 */
	List<CircleHistoryBo> findCircleHisByUserid(String userid, int type, int page, int limit);


	/**
	 * 根据圈子查找
	 * @param circleid
	 * @param type
	 * @param page
	 * @param limit
	 * @return
	 */
	List<CircleHistoryBo> findCircleHisByCricleid(String circleid, int type, int page, int limit);


	WriteResult deleteHis(String id);

	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	WriteResult deleteHisBitch(List<String> ids);


	CircleNoticeBo addNotice(CircleNoticeBo noticeBo);

	/**
	 * 查找圈子公告历史信息
	 * @param circleid
	 * @param page
	 * @param limit
	 * @return
	 */
	List<CircleNoticeBo> findCircleNotice(String circleid, int page, int limit);

	/**
	 * 查找最后一条历史信息
	 * @param circleid
	 * @return
	 */
	CircleNoticeBo findLastNotice(String circleid);

	/**
	 * 删除公告
	 * @param id
	 * @return
	 */
	WriteResult deleteNotice(String id, String userid);

	/**
	 * 更新阅读数
	 * @param id
	 * @param readUsers
	 * @param unReadUsers
	 * @return
	 */
	WriteResult updateNoticeRead(String id, LinkedHashSet<String> readUsers, LinkedHashSet<String> unReadUsers);

	/**
	 * 更新公告内容
	 * @param noticeBo
	 * @return
	 */
	WriteResult updateNotice(CircleNoticeBo noticeBo);


	/**
	 * 更具id查找
	 * @param id
	 * @return
	 */
	CircleNoticeBo findNoticeById(String id);


	/**
	 * 添加
	 * @param showBo
	 * @return
	 */
	CircleShowBo addCircleShow(CircleShowBo showBo);

	/**
	 * 查找
	 * @param circleid
	 * @param page
	 * @param limit
	 * @return
	 */
	List<CircleShowBo> findCircleShows(String circleid, int page, int limit);

	/**
	 * 删除帖子或者聚会时要删除该数据 ,由于帖子id和聚会是唯一的，转发和分享具有另外的主键，可以直接删除目标id
	 * @param targetid
	 * @return
	 */
	WriteResult deleteShow( String targetid);



	/**
	 * 查找未读公告信息
	 * @param userid
	 * @return
	 */
	List<CircleNoticeBo> findUnReadNotices(String userid, String circleid);



	/**
	 * 查找未读公告信息
	 * @param userid
	 * @return
	 */
	List<CircleNoticeBo> findUnReadNotices(String userid, String circleid, int page, int limit);


	/**
	 * 更具ids查找
	 * @param ids
	 * @return
	 */
	List<CircleNoticeBo> findNoticeByIds(String... ids);

	/**
	 * 查找丼活跃人员
	 * @param cirlcid
	 * @param userid
	 * @param position
	 * @param maxDistance
	 * @return
	 */
	GeoResults<CircleHistoryBo> findNearPeopleDis(String cirlcid, String userid, double[] position, double
			maxDistance);

}
