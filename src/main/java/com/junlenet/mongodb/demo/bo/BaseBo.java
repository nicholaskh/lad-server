package com.junlenet.mongodb.demo.bo;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

public class BaseBo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4629611041856330852L;
	
	@Id
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
