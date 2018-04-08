package com.lad.vo;

import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/10/4
 */
@Getter
@Setter
public class BroadcastVo extends BaseVo {

    private String inforid;

    private String sourceUrl;

    private String title;

    private String module;

    private String play_times;

    private String className;

    private String source;

    private String intro;

    private String broadcast_url;

    private int shareNum;

    private int readNum;

    private int thumpsubNum;

    private int commentNum;

    private boolean selfSub;

}
