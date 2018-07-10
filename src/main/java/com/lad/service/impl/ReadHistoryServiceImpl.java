package com.lad.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lad.bo.ReadHistoryBo;
import com.lad.dao.IReadHistoryDao;
import com.lad.service.IReadHistoryService;

@Service("readHistoryService")
public class ReadHistoryServiceImpl implements IReadHistoryService {
	@Autowired
	private IReadHistoryDao readHistoryDao;

	@Override
	public String addReadHistory(ReadHistoryBo historyBo) {
		return readHistoryDao.addReadHistory(historyBo);
	}

	@Override
	public ReadHistoryBo getHistoryByUseridAndNoteId(String userid, String id) {
		return readHistoryDao.getHistoryByUseridAndNoteId(userid, id);
	}

	@Override
	public String updateReadNum(String id,int readNum) {
		return readHistoryDao.updateReadNum(id,readNum);
	}
}
