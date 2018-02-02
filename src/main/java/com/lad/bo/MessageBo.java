package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "message")
public class MessageBo extends BaseBo {

	private String userid;
	private String title;
	private String content;
	private String path;
	//阅读状态，0未读， 1已读
	private int status;

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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
