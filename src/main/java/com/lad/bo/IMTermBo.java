package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "iMTerm")
public class IMTermBo extends BaseBo {
	
	private String term;
	private String userid;
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	
}
