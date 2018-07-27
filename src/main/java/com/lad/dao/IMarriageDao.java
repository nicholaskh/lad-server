package com.lad.dao;

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

public interface IMarriageDao {

	List<WaiterBo> getPublishById(String userId);

	List<OptionBo> getOptions(OptionVo ov);

	WriteResult updateByParams(String id, Map<String, Object> params, Class class1);

	WriteResult deletePublish(String pubId);

	Set<String> getPass(String waiterId);

	WaiterBo findWaiterById(String caresId);

	RequireBo findRequireById(String waiterId);

	String insertPublish(BaseBo bb);

	List<OptionBo> getOptions();

	List<Map> getRecommend(String waiterId, String uid);
	
	List<WaiterBo> getNewPublic(int type,int page,int limit,String uid);

	Map<String, Set<String>> getCareMap(String waiterId);

	WriteResult updateCare(String waiterId, Map<String, Set<String>> map);

	int findPublishNum(String id);

	List<WaiterBo> findListByKeyword(String keyWord,int type,int page, int limit, Class clazz);

	int findPublishGirlNum(String uid);

	List<WaiterBo> getBoysByUserId(String userId);

	List<WaiterBo> getGirlsByUserId(String userId);

	List<OptionBo> getHobbysSupOptions();

	List<OptionBo> getHobbysSonOptions(String id);
	/**
	 * 查询所有职位选项,添加伪数据时使用
	 * @return
	 */
	List<OptionBo> getJobOptions();
	/**
	 * 查询所有薪资选项,添加伪数据时使用
	 * @return
	 */
	List<OptionBo> getSalaryOptions();
	/**
	 * 根据条件查询,添加模拟数据是是用那个
	 * @param criteria
	 * @return
	 */
	List<WaiterBo> findUserCriteria(Criteria criteria);

	WriteResult deleteMany(Criteria criteria,Class clazz);

	WaiterBo findWaiterByNickName(String nickName, String uid);

}
