package com.lad.dao;

import com.lad.bo.NoteBo;
import com.mongodb.WriteResult;

public interface INoteDao extends IBaseDao {
	public NoteBo insert(NoteBo noteBo);

	public NoteBo selectById(String noteId);

	public WriteResult updatePhoto(String noteId, String photo);
}
