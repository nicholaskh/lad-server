package com.lad.dao;

import com.lad.bo.ThumbsupBo;
import com.mongodb.WriteResult;

import java.util.List;

public interface IThumbsupDao extends IBaseDao {
	public ThumbsupBo insert(ThumbsupBo thumbsupBo);

	public List<ThumbsupBo> selectByOwnerId(String ownerId);

	public List<ThumbsupBo> selectByVisitorId(String visitorId);
	
	public ThumbsupBo getByVidAndVisitorid(String vid, String visitorid);
	
	public List<ThumbsupBo> selectByVisitorIdPaged(String startId, boolean gt, int limit, String visitorId);
	
	public List<ThumbsupBo> selectByOwnerIdPaged(String startId, boolean gt, int limit, String ownerId);

	WriteResult delete(String thumbsupId);

	ThumbsupBo findIsDelete(ThumbsupBo thumbsupBo);

	WriteResult updateDelete(String thumbsupId);
}
