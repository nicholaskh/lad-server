package com.lad.service.impl;

import com.lad.bo.OrganizationBo;
import com.lad.dao.IOrganizationDao;
import com.lad.service.IOrganizationService;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service("organizationService")
public class OrganizationServiceImpl implements IOrganizationService {

	@Autowired
	private IOrganizationDao organizationDao;
	
	public OrganizationBo insert(OrganizationBo organizationBo) {
		return organizationDao.insert(organizationBo);
	}

	public List<OrganizationBo> selectByTag(String tag, String sub_tag) {
		return organizationDao.selectByTag(tag, sub_tag);
	}

	public WriteResult updateUsers(String organizationBoId,
			HashSet<String> users) {
		return organizationDao.updateUsers(organizationBoId, users);
	}

	public OrganizationBo get(String organizationBoId) {
		return organizationDao.get(organizationBoId);
	}

	public WriteResult updateUsersApply(String organizationBoId,
			HashSet<String> usersApply) {
		return organizationDao.updateUsersApply(organizationBoId, usersApply);
	}

	public WriteResult updateDescription(String organizationBoId,
			String description) {
		return organizationDao.updateDescription(organizationBoId, description);
	}

	@Override
	public WriteResult updateMutil(OrganizationBo organizationBo) {
		return organizationDao.updateMutil(organizationBo);
	}
}
