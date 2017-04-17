package com.junlenet.mongodb.demo.service;

import java.util.List;

import com.junlenet.mongodb.demo.bo.InfoBo;

public interface IInfoService extends IBaseService {
	public InfoBo insert(InfoBo infoBo);
	public List<InfoBo> selectByOwnerId(String ownerId);
}
