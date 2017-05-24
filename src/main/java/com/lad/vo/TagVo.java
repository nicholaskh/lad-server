package com.lad.vo;

import java.util.HashSet;

public class TagVo extends BaseVo {
	private String userid;
	private HashSet<String> friendsIds;
	private String name;
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public HashSet<String> getFriendsIds() {
		return friendsIds;
	}
	public void setFriendsIds(HashSet<String> friendsIds) {
		this.friendsIds = friendsIds;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
