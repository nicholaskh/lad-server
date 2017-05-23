package com.lad.dao;

import com.lad.bo.IMTermBo;
import com.mongodb.WriteResult;

public interface IIMTermDao extends IBaseDao {
	public IMTermBo insert(IMTermBo iMTermBo);
	public IMTermBo selectByUserid(String userid);
	public WriteResult updateByUserid(String userid,String term);
}
