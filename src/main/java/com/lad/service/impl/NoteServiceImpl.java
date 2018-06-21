package com.lad.service.impl;

import com.lad.bo.CommentBo;
import com.lad.bo.NoteBo;
import com.lad.dao.INoteDao;
import com.lad.service.INoteService;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.GeoResults;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service("noteService")
public class NoteServiceImpl implements INoteService {
	@Autowired
	private INoteDao noteDao;

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
	public List<NoteBo> finyByCreateTime(String circleid, int page, int limit) {
		return noteDao.finyByCreateTime(circleid,page, limit);
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
	public void deleteNote(String noteId, String deleteuid) {
		noteDao.deleteNote(noteId, deleteuid);
	}

	@Override
	public List<NoteBo> selectMyNotes(String userid, int page, int limit) {
		return noteDao.selectMyNotes(userid, page, limit);
	}

	@Override
	public List<NoteBo> finyMyNoteByComment(String userid, int page, int limit) {
		return noteDao.finyMyNoteByComment(userid, page, limit);
	}

	@Override
	public long finyNotesNum(String circleid) {
		return noteDao.finyNotesNum(circleid);
	}

	@Override
	public int selectPeopleNum(String circleid) {
		return noteDao.selectPeopleNum(circleid);
	}

	@Override
	public List<NoteBo> selectCircleNotes(String circleId, int page, int limit) {
		return noteDao.selectCircleNotes(circleId, page, limit);
	}

	@Override
	public WriteResult updateToporEssence(String noteid, int status, int type) {
		return noteDao.updateToporEssence(noteid, status, type);
	}

	@Override
	public List<NoteBo> findByTopEssence(String circleid, int type, int page, int limit) {
		return noteDao.findByTopEssence(circleid, type, page, limit);
	}

	@Override
	public List<NoteBo> findByTopAndEssence(String circleid, int status, int page, int limit) {
		return noteDao.findByTopAndEssence(circleid, status, page, limit);
	}

	@Override
	public List<NoteBo> findNotTopAndEssence(String circleid, int page, int limit) {
		return noteDao.findNotTopAndEssence(circleid, page, limit);
	}

	@Override
	public List<NoteBo> findByDate(String circleid, Date date, int type, int page, int limit) {
		return noteDao.findByDate(circleid, date, type, page, limit);
	}

	@Override
	public WriteResult updateCollectCount(String noteId, int num) {
		return noteDao.updateCollectCount(noteId, num);
	}

	@Override
	public List<NoteBo> selectByTitle(String circleid, String title, String type, int page, int limit) {
		return noteDao.selectByTitle(circleid, title, type, page, limit);
	}

	@Override
	public List<NoteBo> selectByUserid(String circleid, String userid, int page, int limit) {
		return noteDao.selectByUserid(circleid, userid, page, limit);
	}

	@Override
	public List<NoteBo> selectByCreatTime(String circleid, Date startTime, Date endTime, int page, int limit) {
		return noteDao.selectByCreatTime(circleid, startTime, endTime, page, limit);
	}

	@Override
	public List<NoteBo> selectByNoteType(String circleid, String type, int page, int limit) {
		return noteDao.selectByNoteType(circleid, type, page, limit);
	}

	@Override
	public GeoResults<NoteBo> findNearNote(double[] position, int maxDistance, int limit,int page) {
		return noteDao.findNearNote(position, maxDistance, limit,page);
	}
}
