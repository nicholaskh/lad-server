package com.lad.service;

import java.util.List;

import com.lad.bo.BaseBo;
import com.lad.bo.SpouseBaseBo;
import com.lad.bo.SpouseRequireBo;

public interface SpouseService extends IBaseService{
	public void test();

	public String insert(BaseBo baseBo);

	public SpouseBaseBo findBaseById(String baseId);

	public SpouseRequireBo findRequireById(String baseId);

	public List<String> getCaresList(String baseId, String key);	
}
