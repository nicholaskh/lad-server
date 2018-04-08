package com.lad.bo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedHashSet;

/**
 * 功能描述： 资讯订阅收藏类
 * Version: 1.0
 * Time:2017/8/1
 */
@Getter
@Setter
@Document(collection = "subscription")
public class InforSubscriptionBo extends BaseBo {

    private String userid;
    //咨询订阅, 订阅是小分类
    private LinkedHashSet<String> subscriptions = new LinkedHashSet<>();
    //安全订阅
    private LinkedHashSet<String> securitys = new LinkedHashSet<>();
    //咨询收藏，收藏是单条咨询
    private LinkedHashSet<String> collects = new LinkedHashSet<>();
    //广播订阅
    private LinkedHashSet<String> radios = new LinkedHashSet<>();
    //视频订阅
    private LinkedHashSet<String> videos = new LinkedHashSet<>();
    //时政订阅
    private LinkedHashSet<String> dailys = new LinkedHashSet<>();
    //养老订阅
    private LinkedHashSet<String> yanglaos = new LinkedHashSet<>();

    private int type;

}
