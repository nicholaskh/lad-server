package com.lad.service;

import com.lad.bo.IMTermBo;
import com.mongodb.WriteResult;

public interface IIMTermService extends IBaseService {
	public IMTermBo insert(IMTermBo iMTermBo);
	public IMTermBo selectByUserid(String userid);
	public WriteResult updateByUserid(String userid,String term);
}
