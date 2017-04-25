package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "message")
public class MessageBo extends BaseBo {

	private String ownerId;
	private String content;
	private String source;

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
}
