package com.lad.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lad.bo.NoteBo;
import com.lad.dao.INoteDao;
import com.lad.service.INoteService;
import com.mongodb.WriteResult;

@Service("noteService")
public class NoteServiceImpl implements INoteService {
	@Autowired
	private INoteDao noteDao;

	public NoteBo insert(NoteBo noteBo) {
		return noteDao.insert(noteBo);
	}

	public WriteResult updatePhone(String noteId, String photo) {
		return noteDao.updatePhone(noteId, photo);
	}

	public NoteBo selectById(String noteId) {
		return noteDao.selectById(noteId);
	}
}
