package com.lad.dao;

import java.util.List;
import java.util.Map;

import com.lad.bo.BaseBo;
import com.lad.bo.SpouseBaseBo;
import com.lad.bo.SpouseRequireBo;
import com.mongodb.WriteResult;

public interface SpouseDao {
	void test();

	String insert(BaseBo baseBo);

	SpouseBaseBo findBaseById(String baseId);

	SpouseRequireBo findRequireById(String baseId);


	Map<String, List> getCareMap(String spouseId);

	WriteResult updateCare(String spouseId, Map<String, List> map);
}