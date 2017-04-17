package com.junlenet.mongodb.demo.bo;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "info")
public class InfoBo extends BaseBo {

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
