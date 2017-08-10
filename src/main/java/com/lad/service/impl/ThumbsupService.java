package com.lad.service.impl;

import com.lad.bo.ThumbsupBo;
import com.lad.dao.IThumbsupDao;
import com.lad.service.IThumbsupService;
import com.lad.util.Constant;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("thumbsupService")
public class ThumbsupService implements IThumbsupService {

	@Autowired
	private IThumbsupDao thumbsupDao;

	public ThumbsupBo insert(ThumbsupBo thumbsupBo) {
		ThumbsupBo old = thumbsupDao.findIsDelete(thumbsupBo);
		if (old != null) {
			thumbsupDao.updateDelete(thumbsupBo.getId());
			old.setDeleted(Constant.ACTIVITY);
			return old;
		} else {
			return thumbsupDao.insert(thumbsupBo);
		}
	}

	public List<ThumbsupBo> selectByOwnerId(String ownerId) {
		return thumbsupDao.selectByOwnerId(ownerId);
	}

	public List<ThumbsupBo> selectByVisitorId(String visitorId) {
		return thumbsupDao.selectByVisitorId(visitorId);
	}

	public List<ThumbsupBo> selectByOwnerIdPaged(String startId, boolean gt, int limit, String ownerId, int type) {
		return thumbsupDao.selectByOwnerIdPaged(startId, gt, limit, ownerId, type);
	}

	public List<ThumbsupBo> selectByVisitorIdPaged(String startId, boolean gt, int limit, String visitorId) {
		return thumbsupDao.selectByVisitorIdPaged(startId, gt, limit, visitorId);
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
}
