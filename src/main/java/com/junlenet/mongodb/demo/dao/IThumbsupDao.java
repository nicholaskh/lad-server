package com.junlenet.mongodb.demo.dao;

import java.util.List;

import com.junlenet.mongodb.demo.bo.ThumbsupBo;

public interface IThumbsupDao extends IBaseDao {
	public ThumbsupBo insert(ThumbsupBo thumbsupBo);

	public List<ThumbsupBo> selectByOwnerId(String ownerId);

	public List<ThumbsupBo> selectByVisitorId(String visitorId);
}
