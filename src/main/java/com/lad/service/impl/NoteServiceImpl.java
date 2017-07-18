package com.lad.service.impl;

import com.lad.bo.CommentBo;
import com.lad.bo.NoteBo;
import com.lad.dao.ICommentDao;
import com.lad.dao.INoteDao;
import com.lad.service.INoteService;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service("noteService")
public class NoteServiceImpl implements INoteService {
	@Autowired
	private INoteDao noteDao;

	@Autowired
	private ICommentDao commentDao;

	public NoteBo insert(NoteBo noteBo) {
		return noteDao.insert(noteBo);
	}

	public WriteResult updatePhoto(String noteId, LinkedList<String> photos) {
		return noteDao.updatePhoto(noteId, photos);
	}

	public NoteBo selectById(String noteId) {
		return noteDao.selectById(noteId);
	}

	@Override
	public List<NoteBo> finyByCreateTime(String circleid, String startId, boolean gt, int limit) {
		return noteDao.finyByCreateTime(circleid,startId, gt, limit);
	}

	@Override
	public WriteResult thumbSupNote(String noteId, String userid) {
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

	@Override
	public WriteResult updateVisitCount(String noteId) {
		return noteDao.updateVisitCount(noteId);
	}

	@Override
	public WriteResult updateCommentCount(String noteId, long commentcount) {
		return noteDao.updateCommentCount(noteId, commentcount);
	}

	@Override
	public WriteResult updateTransCount(String noteId, long transcount) {
		return noteDao.updateTransCount(noteId, transcount);
	}

	@Override
	public WriteResult updateThumpsubCount(String noteId, long thumpsubcount) {
		return noteDao.updateThumpsubCount(noteId, thumpsubcount);
	}


	@Override
	public WriteResult addComment(String noteid, CommentBo commentBo) {
		return null;
	}

	@Override
	public List<NoteBo> selectTopNotes(String circleid) {
		return noteDao.selectTopNotes(circleid);
	}

	@Override
	public List<NoteBo> selectHotNotes(String circleid) {
		return noteDao.selectHotNotes(circleid);
	}

	@Override
	public void deleteNote(String noteId) {
		noteDao.deleteNote(noteId);
	}

	@Override
	public List<NoteBo> selectMyNotes(String userid, String startId, boolean gt, int limit) {
		return noteDao.selectMyNotes(userid, startId, gt, limit);
	}
}
