package com.lad.dao;

import java.util.List;
import java.util.Map;

import com.lad.bo.BaseBo;
import com.lad.bo.OptionBo;
import com.lad.bo.RequireBo;
import com.lad.bo.WaiterBo;
import com.lad.vo.OptionVo;
import com.lad.vo.WaiterVo;
import com.mongodb.WriteResult;

public interface IMarriageDao {

	List<WaiterBo> getPublishById(String userId);

	String insert(Object obj);

	List<OptionBo> getOptions(OptionVo ov);

	WriteResult updateByParams(String id, Map<String, Object> params, Class class1);

	WriteResult deletePublish(String pubId);

	List<String> getCaresList(String waiterId,String key);

	WaiterBo findWaiterById(String caresId);

	List<String> getUnrecommendList(String waiterId);

	RequireBo findRequireById(String requireId);

	String insertPublish(BaseBo bb);

}
