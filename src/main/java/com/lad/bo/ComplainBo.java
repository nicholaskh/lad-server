package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "complain")
public class ComplainBo extends BaseBo {

	private String userid;
	private String content;
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
