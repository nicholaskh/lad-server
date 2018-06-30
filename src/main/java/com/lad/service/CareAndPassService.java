package com.lad.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.lad.bo.CareAndPassBo;
import com.mongodb.WriteResult;

public interface CareAndPassService {
	// 关注 - 找儿媳
	Map<String,Set<String>> findMarriageCareMap(String mainId);
	// 关注 - 找老伴
	Map<String,Set<String>> findSpouseCareMap(String mainId);
	// 关注 - 找驴友
	Map<String,Set<String>> findTravelersCareMap(String mainId);
	
	// 黑名单 - 找儿媳
	Set<String> findMarriagePassList(String mainId);
	// 黑名单 - 找老伴
	Set<String> findSpousePassList(String mainId);
	// 黑名单 - 找驴友
	Set<String> findTravelersPassList(String mainId);
	
	


	
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
	
	// 测试
	public String test();
	
	
	
	// 添加数据
	public String insert(CareAndPassBo care);
	// 修改数据
	WriteResult updateCare(String situation, String mainId, Map<String, Set<String>> careRoster);
	WriteResult updatePass(String situation, String mainId, Set<String> passRoster);
	
}
