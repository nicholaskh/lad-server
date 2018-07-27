package com.lad.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.lad.bo.BaseBo;
import com.lad.bo.SpouseBaseBo;
import com.lad.bo.SpouseRequireBo;
import com.mongodb.WriteResult;

public interface SpouseService extends IBaseService {
	public void test();

	public String insert(BaseBo baseBo);

	public SpouseBaseBo findBaseById(String baseId);

	public SpouseRequireBo findRequireById(String baseId);

	public WriteResult updateByParams(String spouseId, Map<String, Object> params, Class class1);

	public List<SpouseBaseBo> getNewSpouse(String sex, int page, int limit, String uid);

	public WriteResult deletePublish(String spouseId);

	public SpouseBaseBo getSpouseByUserId(String id);

	public int getNum(String id);

	public List<SpouseBaseBo> findListByKeyword(String keyWord, String sex, int page, int limit,
			Class<SpouseBaseBo> clazz);

	public List<Map> getRecommend(SpouseRequireBo require, String uid, String baseId);

	public int findPublishNum(String uid);

	public WriteResult updateRequireSex(String requireId, String requireSex, Class clazz);

}
