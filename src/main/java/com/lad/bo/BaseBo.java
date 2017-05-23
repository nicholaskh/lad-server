package com.lad.bo;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;

public class BaseBo implements Serializable {

	/**
	 * 
	 */
	@Id
	private String id;

	private Date createTime = new Date();

	private Date updateTime;

	private Integer createuid;

	private Integer updateuid;

	private Integer deleted = 0;

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

	public Integer getDeleted() {
		return deleted;
	}

	public void setDeleted(Integer deleted) {
		this.deleted = deleted;
	}

}
