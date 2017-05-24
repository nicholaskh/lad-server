package com.lad.service;

import java.util.List;

import com.lad.bo.TagBo;
import com.mongodb.WriteResult;

public interface ITagService extends IBaseService {
	public TagBo insert(TagBo tagBo);
	public WriteResult updateFriendsIdsById(TagBo tagBo);
	public List<TagBo> getTagBoListByUserid(String userid);
	public List<TagBo> getTagBoListByUseridAndFrinedid(String userid, String friendid);
	public WriteResult deleteById(String tagId);
	public TagBo get(String tagId);
}
