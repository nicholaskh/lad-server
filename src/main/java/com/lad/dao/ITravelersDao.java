package com.lad.dao;

import java.util.List;

import com.lad.bo.BaseBo;
import com.lad.bo.TravelersBaseBo;
import com.lad.bo.TravelersRequireBo;

public interface ITravelersDao {

	void test();

	String insert(BaseBo baseBo);

	List<TravelersRequireBo> getRequireList(String id);

	TravelersRequireBo getRequireById(String requireId);

	int findPublishNum(String id);


}
