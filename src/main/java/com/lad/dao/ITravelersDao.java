package com.lad.dao;

import java.util.List;

import com.lad.bo.BaseBo;
import com.lad.bo.TravelersBaseBo;
import com.lad.vo.TravelersRequireVo;

public interface ITravelersDao {

	void test();

	String insert(BaseBo baseBo);

	List<TravelersBaseBo> getTravelersByUserId(String id);

	TravelersRequireVo getRequireByBaseId(String id);

}
