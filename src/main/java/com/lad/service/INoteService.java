package com.lad.service;

import com.lad.bo.CommentBo;
import com.lad.bo.NoteBo;
import com.mongodb.WriteResult;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public interface INoteService extends IBaseService {
	public NoteBo insert(NoteBo noteBo);

	public NoteBo selectById(String noteId);

	public WriteResult updatePhoto(String noteId, LinkedList<String> photos);

	/**
	 * 更新帖子访问量
	 * @param noteId
	 * @return
	 */
	WriteResult updateVisitCount(String noteId);

	/**
	 * 更新评论数量
	 * @param noteId
	 * @param commentcount
	 * @return
	 */
	WriteResult updateCommentCount(String noteId, long commentcount);

	/**
	 * 更新转发数量
	 * @param noteId
	 * @param transcount
	 * @return
	 */
	WriteResult updateTransCount(String noteId, long transcount);

	/**
	 * 更新点赞数量
	 * @param noteId
	 * @param thumpsubcount
	 * @return
	 */
	WriteResult updateThumpsubCount(String noteId, long thumpsubcount);

	/**
	 * 获取圈子内最新的帖子
	 * @param circleid
	 * @param page
	 * @param limit
	 * @return
	 */
	List<NoteBo> finyByCreateTime(String circleid, int page, int limit);


	/**
	 * 帖子点赞
	 * @param noteid
	 * @param userid
	 * @return
	 */
	WriteResult thumbSupNote(String noteid, String userid);

	/**
	 * 帖子评论
	 * @param noteid
	 * @return
	 */
	WriteResult addComment(String noteid, CommentBo commentBo);

	/**
	 * 取评论最高前十
	 * @param circleid
	 * @return
	 */
	List<NoteBo> selectByComment(String circleid);

	/**
	 * 精华帖子 取访问量前4
	 * @param circleid
	 * @return
	 */
	List<NoteBo> selectByVisit(String circleid);

	/**
	 * 查找置顶帖子
	 * @param circleid
	 * @return
	 */
	List<NoteBo> selectTopNotes(String circleid);

	/**
	 * 圈子内热门帖子列表
	 * @param circleid
	 * @return
	 */
	List<NoteBo> selectHotNotes(String circleid);

	/**
	 * 删除帖子
	 * @param noteId
	 * @return
	 */
	void deleteNote(String noteId, String deleteuid);

	/**
	 * 我的帖子
	 * @param userid
	 * @param limit
	 * @return
	 */
	List<NoteBo> selectMyNotes(String userid, int page, int limit);

	/**
	 * 获取我被评论的帖子列表
	 * @param userid
	 * @param limit
	 * @return
	 */
	List<NoteBo> finyMyNoteByComment(String userid, int page, int limit);

	/**
	 * 查找圈子类所有帖子
	 * @param circleid
	 * @return
	 */
	long finyNotesNum(String circleid);


	int selectPeopleNum(String circleid);

	/**
	 * 获取圈子内所有帖子
	 * @param circleId
	 * @param limit
	 * @return
	 */
	List<NoteBo> selectCircleNotes(String circleId, int page, int limit);

	/**
	 * 给帖子加精或置顶
	 * @param status
	 * @param type
	 * @return
	 */
	WriteResult updateToporEssence(String noteid, int status, int type);

	/**
	 * 查找加精或置顶帖子
	 * @param circleid
	 * @param type
	 * @return
	 */
	List<NoteBo> findByTopEssence(String circleid, int type, int page, int limit);

	/**
	 * 查找加精和置顶帖子
	 * @param circleid
	 * @return
	 */
	List<NoteBo> findByTopAndEssence(String circleid, int status, int page, int limit);

	/**
	 * 查找既没有加精也没有置顶的帖子
	 * @param circleid
	 * @param page
	 * @param limit
	 * @return
	 */
	List<NoteBo> findNotTopAndEssence(String circleid, int page, int limit);

	/**
	 * 根据帖子类型查找指定日期当天的创建的帖子
	 * @param circleid
	 * @param page
	 * @param limit
	 * @return
	 */
	List<NoteBo> findByDate(String circleid, Date date, int type, int page, int limit);

	/**
	 * 更新收藏数量
	 * @param noteId
	 * @param num
	 * @return
	 */
	WriteResult updateCollectCount(String noteId, int num);

	/**
	 * 根据标题搜索圈子内帖子
	 * @param circleid
	 * @param title
	 * @param page
	 * @param limit
	 * @return
	 */
	List<NoteBo> selectByTitle(String circleid, String title, String type, int page, int limit);


	/**
	 * 根据用户搜索圈子内帖子
	 * @param circleid
	 * @param userid
	 * @param page
	 * @param limit
	 * @return
	 */
	List<NoteBo> selectByUserid(String circleid, String userid, int page, int limit);

	/**
	 * 根据发帖时间搜索帖子
	 * @param circleid
	 * @param startTime
	 * @param endTime
	 * @param page
	 * @param limit
	 * @return
	 */
	List<NoteBo> selectByCreatTime(String circleid, Date startTime, Date endTime,int page, int limit);


	/**
	 * 更具帖子类型搜索
	 * @param circleid
	 * @param type
	 * @param page
	 * @param limit
	 * @return
	 */
	List<NoteBo> selectByNoteType(String circleid, String type, int page, int limit);
}

