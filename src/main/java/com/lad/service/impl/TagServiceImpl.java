package com.lad.service.impl;

import com.lad.bo.TagBo;
import com.lad.dao.ITagDao;
import com.lad.service.ITagService;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;

@Service("tagService")
public class TagServiceImpl implements ITagService {

	@Autowired
	private ITagDao tagDao;
	
	public TagBo insert(TagBo tagBo) {
		return tagDao.insert(tagBo);
	}

	public WriteResult updateFriendsIdsById(TagBo tagBo) {
		return tagDao.updateFriendsIdsById(tagBo);
	}

	public List<TagBo> getTagBoListByUserid(String userid) {
		return tagDao.getTagBoListByUserid(userid);
	}

	public List<TagBo> getTagBoListByUseridAndFrinedid(String userid,
			String friendid) {
		return tagDao.getTagBoListByUseridAndFrinedid(userid, friendid);
	}

	public WriteResult deleteById(String tagId) {
		return tagDao.deleteById(tagId);
	}

	public TagBo get(String tagId) {
		return tagDao.get(tagId);
	}

	@Override
	public WriteResult updateTagName(TagBo tagBo) {
		return tagDao.updateTagName(tagBo);
	}

	@Override
	public TagBo getBynameAndUserid(String tagName, String userid){
		return tagDao.getBynameAndUserid(tagName, userid);
	}

	@Override
	public WriteResult updateTagFriends(String tagid, LinkedHashSet<String> friendsIds) {
		return tagDao.updateTagFriends(tagid, friendsIds);
	}
}
