package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 收藏的聊天信息
 */
@Document(collection = "chatinfo")
public class ChatinfoBo extends BaseBo {
	
	private String content;
	
	private String userid;
	
	private String title;

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
}
