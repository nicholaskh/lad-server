package com.lad.dao;

import java.util.List;
import java.util.Map;

import com.lad.bo.BaseBo;
import com.lad.bo.OptionBo;
import com.lad.bo.RequireBo;
import com.lad.bo.WaiterBo;
import com.lad.vo.OptionVo;
import com.mongodb.WriteResult;

public interface IMarriageDao {

	List<WaiterBo> getPublishById(String userId);

	List<OptionBo> getOptions(OptionVo ov);

	WriteResult updateByParams(String id, Map<String, Object> params, Class class1);

	WriteResult deletePublish(String pubId);

	List<String> getPassList(String waiterId);

	WaiterBo findWaiterById(String caresId);

	List<String> getUnrecommendList(String waiterId);

	RequireBo findRequireById(String waiterId);

	String insertPublish(BaseBo bb);

	List<OptionBo> getOptions();

	List<Map> getRecommend(String waiterId);
	
	List<WaiterBo> getNewPublic(int type,int page,int limit,String uid);

	Map<String, List> getCareMap(String waiterId);

	WriteResult updateCare(String waiterId, Map<String, List> map);

	int findPublishNum(String id);

	List<WaiterBo> findListByKeyword(String keyWord,Class clazz);

}
