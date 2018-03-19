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
	//目标id，帖子
	private String targetid;

	private String circleid;

	// 对贴点赞或者评论表的id
	private String sourceid;
	//0 普通消息，1 评论消息， 2点赞消息
	private int type;

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

	public String getTargetid() {
		return targetid;
	}

	public void setTargetid(String targetid) {
		this.targetid = targetid;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getSourceid() {
		return sourceid;
	}

	public void setSourceid(String sourceid) {
		this.sourceid = sourceid;
	}

	public String getCircleid() {
		return circleid;
	}

	public void setCircleid(String circleid) {
		this.circleid = circleid;
	}
}
