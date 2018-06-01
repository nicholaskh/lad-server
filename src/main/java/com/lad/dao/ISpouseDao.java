package com.lad.dao;

import java.util.List;
import java.util.Map;

import com.lad.bo.BaseBo;
import com.lad.bo.SpouseBaseBo;
import com.lad.bo.SpouseRequireBo;
import com.mongodb.WriteResult;

public interface ISpouseDao {
	void test();

	String insert(BaseBo baseBo);

	SpouseBaseBo findBaseById(String baseId);

	SpouseRequireBo findRequireById(String baseId);


	Map<String, List> getCareMap(String spouseId);

	WriteResult updateCare(String spouseId, Map<String, List> map);

	List<String> getPassList(String spouseId);

	WriteResult updateByParams(String spouseId, Map<String, Object> params, Class class1);

	List<SpouseBaseBo> getNewSpouse(int sex,int page,int limit,String uid);

	WriteResult deletePublish(String spouseId);

	SpouseBaseBo getSpouseByUserId(String uid);

}
