package com.junlenet.mongodb.demo.bo;

import java.io.Serializable;
import java.util.LinkedList;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="message")
public class MessageBo extends BaseBo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String content;
	
	private LinkedList<String> thumbsup_ids;
	
	private String owner_id;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public LinkedList<String> getThumbsup_ids() {
		return thumbsup_ids;
	}

	public void setThumbsup_ids(LinkedList<String> thumbsup_ids) {
		this.thumbsup_ids = thumbsup_ids;
	}

	public String getOwner_id() {
		return owner_id;
	}

	public void setOwner_id(String owner_id) {
		this.owner_id = owner_id;
	}
}
