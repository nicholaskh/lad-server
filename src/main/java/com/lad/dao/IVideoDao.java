package com.lad.dao;

import com.lad.scrapybo.InforBo;
import com.lad.scrapybo.VideoBo;
import com.mongodb.WriteResult;

import java.util.HashSet;
import java.util.LinkedList;
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

    List<VideoBo> selectClassByGroups(String module);



    List<VideoBo> findByClassNamePage(String module, String className, int start, int end);


    /**
     * 
     * @param modules
     * @param classNames
     * @return
     */
    List<VideoBo> selectClassByGroups(HashSet<String> modules, HashSet<String> classNames);

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

    /**
     * 根据id批量查找
     * @param videoIds
     * @return
     */
    List<VideoBo> findVideoByIds(List<String> videoIds);

    /**
     * 排除已查询信息
     * @param modules
     * @param classNames
     * @param limit
     * @return
     */
    List<VideoBo> findByLimit(LinkedList<String> modules, LinkedList<String> classNames, int limit);


    /**
     * 查找当前合集下第一条信息
     * @param modules
     * @param classNames
     * @return
     */
    VideoBo findVideoByFirst(String modules, String classNames);

    long findByClassNameCount(String module, String className);

    /**
     * 根据名称匹配
     * @param title
     * @param page
     * @param limit
     * @return
     */
    List<VideoBo> findByTitleRegex(String title, int page, int limit);

}
