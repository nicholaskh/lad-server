package com.lad.dao;

import java.util.List;

import com.lad.bo.BaseBo;
import com.lad.bo.SpouseBaseBo;
import com.lad.bo.SpouseRequireBo;

public interface SpouseDao {
	void test();

	String insert(BaseBo baseBo);

	SpouseBaseBo findBaseById(String baseId);

	SpouseRequireBo findRequireById(String baseId);

	List<String> getCaresList(String baseId, String key);
}
