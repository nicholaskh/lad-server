package com.lad.service;

import java.util.List;

import com.lad.bo.BaseBo;
import com.lad.bo.TravelersBaseBo;
import com.lad.vo.TravelersRequireVo;

public interface TravelersService extends IBaseService {
	public void test();

	public String insert(BaseBo baseBo);

	public List<TravelersBaseBo> getTravelersByUserId(String id);

	public TravelersRequireVo getRequireByBaseId(String id);
}
