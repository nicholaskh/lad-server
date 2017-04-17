package com.junlenet.mongodb.demo.dao;

import java.util.List;

import com.junlenet.mongodb.demo.bo.InfoBo;

public interface IInfoDao extends IBaseDao {
	public InfoBo insert(InfoBo infoBo);
	public List<InfoBo> selectByOwnerId(String ownerId);
}
