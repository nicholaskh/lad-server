package com.lad.dao;

import java.util.List;
import java.util.Map;

import com.lad.bo.BaseBo;
import com.lad.bo.TravelersRequireBo;
import com.mongodb.WriteResult;

public interface ITravelersDao {

	void test();

	String insert(BaseBo baseBo);

	List<TravelersRequireBo> getRequireList(String id);

	TravelersRequireBo getRequireById(String requireId);

	int findPublishNum(String id);

	List<TravelersRequireBo> getNewTravelers(int page, int limit, String id);

	WriteResult updateByIdAndParams(String requireId, Map<String, Object> params);

	WriteResult deletePublish(String requireId);

	List<TravelersRequireBo> findListByKeyword(String keyWord, int page, int limit, Class<TravelersRequireBo> clazz);

	List<Map> getRecommend(TravelersRequireBo require);
}
