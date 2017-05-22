package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chatroomone")
public class ChatroomoneBo extends BaseBo {
	private String name;
	private String userid;
	private String friendid;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getFriendid() {
		return friendid;
	}

	public void setFriendid(String friendid) {
		this.friendid = friendid;
	}

}
