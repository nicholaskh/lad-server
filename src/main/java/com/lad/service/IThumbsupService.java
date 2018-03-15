package com.lad.service;

import com.lad.bo.ThumbsupBo;
import com.mongodb.WriteResult;

import java.util.List;

public interface IThumbsupService extends IBaseService {
	public ThumbsupBo insert(ThumbsupBo thumbsupBo);

	public ThumbsupBo getByVidAndVisitorid(String vid, String visitorid);

	public List<ThumbsupBo> selectByOwnerId(String ownerId);

	public List<ThumbsupBo> selectByVisitorId(String visitorId);

	public List<ThumbsupBo> selectByVisitorIdPaged(int page, int limit, String visitorId, int type);

	public List<ThumbsupBo> selectByOwnerIdPaged(int page, int limit, String ownerId, int type);

	WriteResult deleteById(String thumbsupId);

	/**
	 * 获取当前点赞数量
	 * @param ownerId
	 * @return
	 */
	long selectByOwnerIdCount(String ownerId);
	

	ThumbsupBo findHaveOwenidAndVisitorid(String vid, String visitorid);
	

	WriteResult udateDeleteById(String thumbsupId);


	long selectCountByOwnerId(String ownerId, int type);


	ThumbsupBo selectById(String thumbsupId);
}
