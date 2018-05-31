package com.lad.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.lad.bo.BaseBo;
import com.lad.bo.OptionBo;
import com.lad.bo.RequireBo;
import com.lad.bo.WaiterBo;
import com.lad.vo.OptionVo;
import com.lad.vo.RequireVo;
import com.lad.vo.WaiterVo;
import com.mongodb.WriteResult;

public interface IMarriageService extends IBaseService {
//	根据当前用户id查找发布的信息
	public List<WaiterBo>getPublishById(String userId);
	
//	查看发布消息的具体信息,返回消息时分别将基本资料与要求封装到Map
	public Map<String,Object> getPublishDescById(String WaiterId);
//	添加发布,返回waiterId
//	public String insertPublish(WaiterBo wb,RequireBo rb); 

//	修改儿女资料
	public WriteResult updateWaiter(WaiterVo wv);
//	修改择婿(媳)意向
	public WriteResult updateRequire(RequireVo rv);
//	取消发布
	public WriteResult deletePublish(String pubId);
//	获取选项列表
	public List<OptionBo> getOptions(OptionVo ov);
//	 获取/刷新推荐
	public List<Map> getRecommend(String waiterId);
//	不再推荐
	public List<WaiterBo> addUnRecommend(String WaiterId,String unRecommendId);
//	添加关注
	public List<WaiterBo> addCare(String WaiterId,String CareId); 
//	移除关注
	public WriteResult deleteCare(String WaiterId,String CareId);
//	获取关注列表,返回List<Waiter>
	public List<WaiterBo> getCares(String WaiterId);
//	上传照片,返回地址
	public List<String> insertImage(File[] image);
//	上传多张照片
//	insetImages(File[] images)
//	获取随即昵称
	public String getNickName();
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


	
}
