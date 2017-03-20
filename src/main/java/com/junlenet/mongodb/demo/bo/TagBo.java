package com.junlenet.mongodb.demo.bo;

import java.io.Serializable;

/**
 * 标签BO
 * @author huweijun
 * @date 2016年7月10日 上午9:33:33
 */
public class TagBo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3859783006465879631L;
	
	private String name;
	
	private String code;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	
	

}
