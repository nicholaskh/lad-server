package com.junlenet.mongodb.demo.bo;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "feedback")
public class FeedbackBo extends BaseBo {
	
	private String image;
	private String content;
	private String contactInfo;
	private String ownerId;
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getContactInfo() {
		return contactInfo;
	}
	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}
	public String getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
}
