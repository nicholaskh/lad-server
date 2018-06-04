package com.lad.dao;

import java.util.List;
import java.util.Map;

import com.lad.bo.CareAndPassBo;
import com.mongodb.WriteResult;

public interface CareAndPassDao {


	// 关注 - 找儿媳
	Map<String,List<String>> findMarriageCareMap(String mainId);
	// 关注 - 找老伴
	Map<String,List<String>> findSpouseCareMap(String mainId);
	// 关注 - 找驴友
	Map<String,List<String>> findTravelersCareMap(String mainId);
	
	// 黑名单 - 找儿媳
	List<String> findMarriagePassList(String mainId);
	// 黑名单 - 找老伴
	List<String> findSpousePassList(String mainId);
	// 黑名单 - 找驴友
	List<String> findTravelersPassList(String mainId);
	
	// 关注 - 找儿媳
	CareAndPassBo findMarriageCare(String mainId);
	// 关注 - 找老伴
	CareAndPassBo findSpouseCare(String mainId);
	// 关注 - 找驴友
	CareAndPassBo findTravelersCare(String mainId);
	
	// 拉黑 - 找儿媳
	CareAndPassBo findMarriagePass(String mainId);
	// 拉黑 - 找老伴
	CareAndPassBo findSpousePass(String mainId);
	// 拉黑 - 找驴友
	CareAndPassBo findTravelersPass(String mainId);

	String test();
	// 添加数据
	String insert(CareAndPassBo care);
	// 修改数据
	WriteResult updateCare(String situation, String mainId, Map<String, List<String>> careRoster);
	WriteResult updatePass(String situation, String mainId, List<String> passRoster);


}
