package com.lad.service.impl;

import com.lad.bo.ThumbsupBo;
import com.lad.dao.IThumbsupDao;
import com.lad.service.IThumbsupService;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("thumbsupService")
public class ThumbsupService implements IThumbsupService {

	@Autowired
	private IThumbsupDao thumbsupDao;

	public ThumbsupBo insert(ThumbsupBo thumbsupBo) {
		return thumbsupDao.insert(thumbsupBo);
	}

	public List<ThumbsupBo> selectByOwnerId(String ownerId) {
		return thumbsupDao.selectByOwnerId(ownerId);
	}

	public List<ThumbsupBo> selectByVisitorId(String visitorId) {
		return thumbsupDao.selectByVisitorId(visitorId);
	}

	public List<ThumbsupBo> selectByOwnerIdPaged(int page, int limit, String ownerId, int type) {
		return thumbsupDao.selectByOwnerIdPaged(page, limit, ownerId, type);
	}

	public List<ThumbsupBo> selectByVisitorIdPaged(int page, int limit, String visitorId, int
			type) {
		return thumbsupDao.selectByVisitorIdPaged(page, limit, visitorId, type);
	}

	public ThumbsupBo getByVidAndVisitorid(String vid, String visitorid) {
		return thumbsupDao.getByVidAndVisitorid(vid, visitorid);
	}

	@Override
	public WriteResult deleteById(String thumbsupId) {
		return thumbsupDao.delete(thumbsupId);
	}

	@Override
	public long selectByOwnerIdCount(String ownerId) {
		return thumbsupDao.selectByOwnerIdCount(ownerId);
	}

	@Override
	public ThumbsupBo findHaveOwenidAndVisitorid(String owenrid, String visitorid) {
		return thumbsupDao.findIsDelete(owenrid, visitorid);
	}

	@Override
	public WriteResult udateDeleteById(String thumbsupId) {
		return thumbsupDao.updateDelete(thumbsupId);
	}
}
