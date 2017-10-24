package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedHashSet;

@Document(collection = "tag")
public class TagBo extends BaseBo {
	
	private String userid;
	
	private LinkedHashSet<String> friendsIds = new LinkedHashSet<String>();
	//标签
	private String name;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public LinkedHashSet<String> getFriendsIds() {
		return friendsIds;
	}

	public void setFriendsIds(LinkedHashSet<String> friendsIds) {
		this.friendsIds = friendsIds;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
