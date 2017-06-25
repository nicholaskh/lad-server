package com.lad.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lad.bo.NoteBo;
import com.lad.dao.INoteDao;
import com.lad.service.INoteService;
import com.mongodb.WriteResult;

import java.util.List;

@Service("noteService")
public class NoteServiceImpl implements INoteService {
	@Autowired
	private INoteDao noteDao;

	public NoteBo insert(NoteBo noteBo) {
		return noteDao.insert(noteBo);
	}

	public WriteResult updatePhoto(String noteId, String photo) {
		return noteDao.updatePhoto(noteId, photo);
	}

	public NoteBo selectById(String noteId) {
		return noteDao.selectById(noteId);
	}

	@Override
	public WriteResult updateVisit(String noteId, long visitcount) {
		return noteDao.updateVisit(noteId, visitcount);
	}

	@Override
	public List<NoteBo> finyByCreateTime(String circleid, String startId, boolean gt, int limit) {
		return noteDao.finyByCreateTime(circleid,startId, gt, limit);
	}

	@Override
	public WriteResult thumbSupNote(String circleid, String userid) {
		return null;
	}

	@Override
	public List<NoteBo> selectByComment(String circleid) {
		return noteDao.selectByComment(circleid);
	}

	@Override
	public List<NoteBo> selectByVisit(String circleid) {
		return noteDao.selectByVisit(circleid);
	}
}
