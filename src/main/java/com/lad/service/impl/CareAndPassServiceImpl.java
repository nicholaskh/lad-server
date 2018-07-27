package com.lad.service.impl;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lad.bo.CareAndPassBo;
import com.lad.dao.CareAndPassDao;
import com.lad.service.CareAndPassService;
import com.mongodb.WriteResult;
@Service("careAndPassService")
public class CareAndPassServiceImpl implements CareAndPassService {
	@Autowired
	private CareAndPassDao careAndPassDao;
	
	@Override
	public Map<String, Set<String>> findMarriageCareMap(String mainId) {
		return careAndPassDao.findMarriageCareMap(mainId);
	}

	@Override
	public Map<String, Set<String>> findSpouseCareMap(String mainId) {
		return careAndPassDao.findSpouseCareMap(mainId);
	}

	@Override
	public Map<String, Set<String>> findTravelersCareMap(String mainId) {
		return careAndPassDao.findTravelersCareMap(mainId);
	}
	
	@Override
	public String insert(CareAndPassBo care) {
		return careAndPassDao.insert(care);
	}

	@Override
	public CareAndPassBo findMarriageCare(String mainId) {
		return careAndPassDao.findMarriageCare(mainId);
	}

	@Override
	public CareAndPassBo findSpouseCare(String mainId) {
		return careAndPassDao.findSpouseCare(mainId);
	}

	@Override
	public CareAndPassBo findTravelersCare(String mainId) {
		return careAndPassDao.findTravelersCare(mainId);
	}

	@Override
	public CareAndPassBo findMarriagePass(String mainId) {
		return careAndPassDao.findMarriagePass(mainId);
	}

	@Override
	public CareAndPassBo findSpousePass(String mainId) {
		return careAndPassDao.findSpousePass(mainId);
	}

	@Override
	public CareAndPassBo findTravelersPass(String mainId) {
		return careAndPassDao.findTravelersPass(mainId);
	}

	@Override
	public String test() {
		return careAndPassDao.test();
	}

	@Override
	public WriteResult updateCare(String situation, String mainId, Map<String, Set<String>> careRoster) {
		return careAndPassDao.updateCare(situation,mainId, careRoster);
	}
	
	@Override
	public WriteResult updatePass(String situation, String mainId, Set<String> passRoster) {
		return careAndPassDao.updatePass(situation, mainId, passRoster);
	}
	

	@Override
	public Set<String> findMarriagePassList(String mainId) {
		return careAndPassDao.findMarriagePassList(mainId);
	}

	@Override
	public Set<String> findSpousePassList(String mainId) {
		return careAndPassDao.findSpousePassList(mainId);
	}

	@Override
	public Set<String> findTravelersPassList(String mainId) {
		return careAndPassDao.findTravelersPassList(mainId);
	}







}
