package com.junlenet.mongodb.demo.bo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="t_blog")
public class BlogBo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2751374207176014391L;
	
	@Id
	private String id; //对应文档里面的 _id 

	
	private String authorName;
	
	private Date createDate;
	
	private String title;
	
	private String content;
	
	private List<TagBo> tags;

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
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

	public List<TagBo> getTags() {
		return tags;
	}

	public void setTags(List<TagBo> tags) {
		this.tags = tags;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
