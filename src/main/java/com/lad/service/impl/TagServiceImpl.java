package com.lad.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lad.bo.TagBo;
import com.lad.dao.ITagDao;
import com.lad.service.ITagService;
import com.mongodb.WriteResult;

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

}
