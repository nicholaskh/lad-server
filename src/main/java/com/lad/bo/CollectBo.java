package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedHashSet;

/**
 * 收藏的聊天信息
 */
@Document(collection = "collect")
public class CollectBo extends BaseBo {
	
	private String content;
	
	private String userid;
	
	private String title;

	private String path;

	private int type;
	//子分类，在url中区分文章、帖子、聚会、圈子
	private int sub_type;

	private String targetid;

	//用户自定义分类
	private LinkedHashSet<String> userTags = new LinkedHashSet<>();


	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTargetid() {
		return targetid;
	}

	public void setTargetid(String targetid) {
		this.targetid = targetid;
	}

	public int getSub_type() {
		return sub_type;
	}

	public void setSub_type(int sub_type) {
		this.sub_type = sub_type;
	}

	public LinkedHashSet<String> getUserTags() {
		return userTags;
	}

	public void setUserTags(LinkedHashSet<String> userTags) {
		this.userTags = userTags;
	}
}
