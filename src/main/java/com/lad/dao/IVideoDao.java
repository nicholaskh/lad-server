package com.lad.dao;

import com.lad.scrapybo.VideoBo;

import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/27
 */
public interface IVideoDao {
    
    List<VideoBo> findByPage(String groupName, int page, int limit);

    VideoBo findById(String id);

    List<VideoBo> selectGroups();

    List<VideoBo> selectClassByGroups(String groupName);

}
