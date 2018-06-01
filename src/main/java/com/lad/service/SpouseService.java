package com.lad.service;

import java.util.List;
import java.util.Map;

import com.lad.bo.BaseBo;
import com.lad.bo.SpouseBaseBo;
import com.lad.bo.SpouseRequireBo;
import com.mongodb.WriteResult;

public interface SpouseService extends IBaseService{
	public void test();

	public String insert(BaseBo baseBo);

	public SpouseBaseBo findBaseById(String baseId);

	public SpouseRequireBo findRequireById(String baseId);


	public Map<String, List> getCareMap(String spouseId);

	public WriteResult updateCare(String spouseId, Map<String, List> map);

	public List<String> getPassList(String spouseId);

	public WriteResult updateByParams(String spouseId, Map<String, Object> params, Class<SpouseBaseBo> class1);

	public List<SpouseBaseBo> getNewSpouse(int sex,int page,int limit,String uid);

	public WriteResult deletePublish(String spouseId);

	public WriteResult updateById(Map params, String spouseId, Class<SpouseBaseBo> class1);	
}
