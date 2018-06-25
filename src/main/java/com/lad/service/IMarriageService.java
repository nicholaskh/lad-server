package com.lad.service;

import java.util.List;
import java.util.Map;

import com.lad.bo.BaseBo;
import com.lad.bo.OptionBo;
import com.lad.bo.RequireBo;
import com.lad.bo.WaiterBo;
import com.lad.vo.OptionVo;
import com.mongodb.WriteResult;

public interface IMarriageService extends IBaseService {
//	根据当前用户id查找发布的信息
	public List<WaiterBo>getPublishById(String userId);

//	添加发布,返回waiterId
//	public String insertPublish(WaiterBo wb,RequireBo rb); 


//	取消发布
	public WriteResult deletePublish(String pubId);
//	获取选项列表
	public List<OptionBo> getOptions(OptionVo ov);
//	 获取/刷新推荐
	public List<Map> getRecommend(String waiterId);

//  更新
	public WriteResult updateByParams(String id, Map<String, Object> params, Class class1);

	public List<String> getPassList(String waiterId);

	public WaiterBo findWaiterById(String caresId);

	public List<String> getUnrecommendList(String waiterId);

	public RequireBo findRequireById(String waiterId);

	public String insertPublish(BaseBo bb);

	public List<OptionBo> getOptions();

	public List<WaiterBo> getNewPublish(int type, int page, int limit,String userId);

	public Map<String, List> getCareMap(String waiterId);

	public WriteResult updateCare(String waiterId, Map<String, List> map);

	public int findPublishNum(String id);

	public List<WaiterBo> findListByKeyword(String keyWord,int type,int page, int limit, Class clazz);

	public int findPublishGirlNum(String uid);

	public List<WaiterBo> getBoysByUserId(String userId);

	public List<WaiterBo> getGirlsByUserId(String userId);


	
}
