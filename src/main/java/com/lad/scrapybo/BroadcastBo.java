package com.lad.scrapybo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * 功能描述：广播
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/29
 */
@Document(collection = "broadcast")
public class BroadcastBo implements Serializable {

    @Id
    private String id;

    private String sourceUrl;

    private String title;

    private String module;

    private String play_times;

    private String className;

    private String source;

    private String intro;

    private String broadcast_url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getPlay_times() {
        return play_times;
    }

    public void setPlay_times(String play_times) {
        this.play_times = play_times;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getBroadcast_url() {
        return broadcast_url;
    }

    public void setBroadcast_url(String broadcast_url) {
        this.broadcast_url = broadcast_url;
    }
}
