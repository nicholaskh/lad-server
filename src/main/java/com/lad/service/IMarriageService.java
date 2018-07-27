package com.lad.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.mongodb.core.query.Criteria;

import com.lad.bo.BaseBo;
import com.lad.bo.OptionBo;
import com.lad.bo.RequireBo;
import com.lad.bo.UserBo;
import com.lad.bo.WaiterBo;
import com.lad.vo.OptionVo;
import com.mongodb.WriteResult;

public interface IMarriageService extends IBaseService {
	// 根据当前用户id查找发布的信息
	public List<WaiterBo> getPublishById(String userId);

	// 取消发布
	public WriteResult deletePublish(String pubId);

	// 获取选项列表
	public List<OptionBo> getOptions(OptionVo ov);

	// 匹配推荐
	public List<Map> getRecommend(String waiterId, String uid);

	// 更新
	public WriteResult updateByParams(String id, Map<String, Object> params, Class class1);

	public Set<String> getPass(String waiterId);

	public WaiterBo findWaiterById(String caresId);

	public RequireBo findRequireById(String waiterId);

	public String insertPublish(BaseBo bb);

	public List<OptionBo> getOptions();

	public List<WaiterBo> getNewPublish(int type, int page, int limit, String userId);

	public Map<String, Set<String>> getCareMap(String waiterId);

	public WriteResult updateCare(String waiterId, Map<String, Set<String>> map);

	public int findPublishNum(String id);

	public List<WaiterBo> findListByKeyword(String keyWord, int type, int page, int limit, Class clazz);

	public int findPublishGirlNum(String uid);

	public List<WaiterBo> getBoysByUserId(String userId);

	public List<WaiterBo> getGirlsByUserId(String userId);

	public List<OptionBo> getHobbysSupOptions();

	public List<OptionBo> getHobbysSonOptions(String id);

	/**
	 * 查询所有职位选项,添加伪数据时使用
	 * 
	 * @return
	 */
	public List<OptionBo> getJobOptions();

	/**
	 * 查询所有薪资选项,添加伪数据时使用
	 * 
	 * @return
	 */
	public List<OptionBo> getSalaryOptions();

	/**
	 * 根据条件查询,添加模拟数据是是用那个
	 * 
	 * @param criteria
	 * @return
	 */
	public List<WaiterBo> findUserCriteria(Criteria criteria);

	/**
	 * 批量删除
	 * 
	 * @param list
	 * @return
	 */
	public WriteResult deleteMany(Criteria criteria,Class clazz);

	public WaiterBo findWaiterByNickName(String nickName, String uid);

}
