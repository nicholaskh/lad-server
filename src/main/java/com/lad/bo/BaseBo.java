package com.lad.bo;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;

public class BaseBo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4629611041856330852L;
	
	@Id
	private String id;
	
	private Date createTime;
	
	private Date updateTime;
	
	private Integer createuid;
	
	private Integer updateuid;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getCreateuid() {
		return createuid;
	}

	public void setCreateuid(Integer createuid) {
		this.createuid = createuid;
	}

	public Integer getUpdateuid() {
		return updateuid;
	}

	public void setUpdateuid(Integer updateuid) {
		this.updateuid = updateuid;
	}

}
