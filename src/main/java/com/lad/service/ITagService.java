package com.lad.service;

import com.lad.bo.TagBo;
import com.mongodb.WriteResult;

import java.util.List;

public interface ITagService extends IBaseService {
	public TagBo insert(TagBo tagBo);
	public WriteResult updateFriendsIdsById(TagBo tagBo);
	public List<TagBo> getTagBoListByUserid(String userid);
	public List<TagBo> getTagBoListByUseridAndFrinedid(String userid, String friendid);
	public WriteResult deleteById(String tagId);
	public TagBo get(String tagId);

	WriteResult updateTagName(TagBo tagBo);

	TagBo getBynameAndUserid(String tagName, String userid);
}
