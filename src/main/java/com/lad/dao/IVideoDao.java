package com.lad.dao;

import com.lad.scrapybo.VideoBo;
import com.mongodb.WriteResult;

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

    /**
     * 更新缩略图
     * @param id
     * @param pic
     * @return
     */
    WriteResult updatePicById(String id, String pic);

    /**
     * 按照条数查找
     * @param limit
     * @return
     */
    List<VideoBo> findByLimit(int limit);

    /**
     * 更新各组访问量
     * @param inforid
     * @param type
     * @param num
     * @return
     */
    WriteResult updateVideoNum(String inforid, int type, int num);

}
