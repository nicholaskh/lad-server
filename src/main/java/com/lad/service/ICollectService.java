package com.lad.service;

import com.lad.bo.CollectBo;
import com.lad.bo.UserTagBo;
import com.mongodb.WriteResult;

import java.util.LinkedHashSet;
import java.util.List;

public interface ICollectService {
	
	/**
	 * 收藏聊天记录
	 * @param collectBo 聊天记录
	 * @return 聊天信息
	 */
	CollectBo insert(CollectBo collectBo);

	/**
	 * 
	 * @param id
	 * @return
	 */
	CollectBo findById(String id);
	

	WriteResult delete(String chatId);
	/**
	 * 
	 * @param userid
	 * @param start_id
	 * @param limit
	 * @return
	 */
	List<CollectBo> findChatByUserid(String userid, String start_id, int limit, int type);

	/**
	 * 更具类型查找
	 * @param userid
	 * @param page
	 * @param limit
	 * @return
	 */
	List<CollectBo> findByUseridAndType(String userid,int page, int limit, int type);

	/**
	 * 查找所有
	 * @param userid
	 * @param page
	 * @return
	 */
	List<CollectBo> findAllByUserid(String userid, int page, int limit);

	/**
	 * 更新收藏标签
	 * @param id
	 * @param userTages
	 * @return
	 */
	WriteResult updateTags(String id, LinkedHashSet<String> userTages);

	/**
	 * 更具关键字或者分类类型
	 * @param keyword
	 * @return
	 */
	List<CollectBo> findByKeyword(String keyword);

	/**
	 * 更具用户分类
	 * @param tag
	 * @return
	 */
	List<CollectBo> findByTag(String tag, String userid, int page , int limit);


	UserTagBo insertTag(UserTagBo tagBo);


	WriteResult deleteTag(String id);


	UserTagBo findByTagName(String name, String userid);


	List<UserTagBo> findTagByUserid(String userid, int type);


	WriteResult updateTagTimes(String name, String userid, int type);

}
