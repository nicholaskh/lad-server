package com.lad.service;

import java.util.List;

import com.lad.bo.BaseBo;
import com.lad.bo.TravelersRequireBo;

public interface TravelersService extends IBaseService {
	public void test();

	public String insert(BaseBo baseBo);

	public List<TravelersRequireBo> getRequireList(String id);

	public TravelersRequireBo getRequireById(String requireId);

	public int findPublishNum(String id);



}
