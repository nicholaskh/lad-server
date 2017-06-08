package com.lad.service;

import com.lad.bo.NoteBo;
import com.mongodb.WriteResult;

public interface INoteService extends IBaseService {
	public NoteBo insert(NoteBo noteBo);

	public NoteBo selectById(String noteId);

	public WriteResult updatePhoto(String noteId, String photo);

}
