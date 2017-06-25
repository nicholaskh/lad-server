package com.lad.service;

import com.lad.bo.NoteBo;
import com.mongodb.WriteResult;

import java.util.List;

public interface INoteService extends IBaseService {
	public NoteBo insert(NoteBo noteBo);

	public NoteBo selectById(String noteId);

	public WriteResult updatePhoto(String noteId, String photo);

	/**
	 * 更新帖子访问量
	 * @param noteId
	 * @param visitcount
	 * @return
	 */
	WriteResult updateVisit(String noteId, long visitcount);

	/**
	 * 获取圈子内最新的帖子
	 * @param circleid
	 * @param startId
	 * @param gt
	 * @param limit
	 * @return
	 */
	List<NoteBo> finyByCreateTime(String circleid, String startId, boolean gt, int limit);


	/**
	 * 帖子点赞
	 * @param circleid
	 * @param userid
	 * @return
	 */
	WriteResult thumbSupNote(String circleid, String userid);

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


}
