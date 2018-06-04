package com.lad.service;

import java.util.List;
import java.util.Map;

import com.lad.bo.BaseBo;
import com.lad.bo.TravelersRequireBo;
import com.mongodb.WriteResult;

public interface TravelersService extends IBaseService {
	public void test();

	public String insert(BaseBo baseBo);

	public List<TravelersRequireBo> getRequireList(String id);

	public TravelersRequireBo getRequireById(String requireId);

	public int findPublishNum(String id);

	public List<TravelersRequireBo> getNewTravelers(int page, int limit, String id);

	public WriteResult updateByIdAndParams(String requireId, Map<String, Object> params);



}
