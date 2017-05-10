package com.lad.service;

import java.util.List;

import com.lad.bo.ThumbsupBo;

public interface IThumbsupService extends IBaseService {
	public ThumbsupBo insert(ThumbsupBo thumbsupBo);

	public ThumbsupBo getByVidAndVisitorid(String vid, String visitorid);

	public List<ThumbsupBo> selectByOwnerId(String ownerId);

	public List<ThumbsupBo> selectByVisitorId(String visitorId);

	public List<ThumbsupBo> selectByVisitorIdPaged(String startId, boolean gt, int limit, String visitorId);

	public List<ThumbsupBo> selectByOwnerIdPaged(String startId, boolean gt, int limit, String ownerId);
}
