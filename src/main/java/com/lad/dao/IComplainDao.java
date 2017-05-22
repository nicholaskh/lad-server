package com.lad.dao;

import com.lad.bo.ComplainBo;

public interface IComplainDao extends IBaseDao {

	public ComplainBo insert(ComplainBo complainBo);
}
