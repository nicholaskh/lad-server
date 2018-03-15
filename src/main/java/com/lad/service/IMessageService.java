package com.lad.service;

import java.util.List;

import com.lad.bo.MessageBo;
import com.mongodb.WriteResult;

public interface IMessageService extends IBaseService {


	MessageBo insert(MessageBo messageBo);

	MessageBo selectById(String messageId);

	List<MessageBo> findByUserId(String userId, int status, int page, int limit);

	WriteResult deleteMessage(String id);

	WriteResult deleteMessages(List<String> ids);

	/**
	 * 更新消息状态
	 * @param id
	 * @param status
	 * @return
	 */
	WriteResult updateMessage(String id, int status);


	/**
	 * 批量更新消息状态
	 * @param id
	 * @param status
	 * @return
	 */
	WriteResult betchUpdateMessage(List<String> ids, int status);

	/**
	 * 查找当前帖子未读的点赞和评论数据
	 * @param userId
	 * @param noteid
	 * @param status
	 * @return
	 */
	List<MessageBo> findUnReadByNoteId(String noteid, int status);


	/**
	 * 查找被点赞或评论的消息
	 * @param sourceid
	 * @param type
	 * @return
	 */
	MessageBo findMessageBySource(String sourceid, int type);

	WriteResult deleteMessageBySource(String sourceid, int type);

	/**
	 * 删除指定帖子的所有消息
	 * @param noteid
	 * @param type
	 * @return
	 */
	WriteResult deleteMessageByNoteid(String noteid, int type);


	/**
	 * 查询个人在当前圈子里面的动态信息表
	 * @param userid
	 * @return
	 */
	List<MessageBo> findUnReadByMyUserid(String userid, String circleid);
}
