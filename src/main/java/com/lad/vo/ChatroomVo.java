package com.lad.vo;

import java.util.HashSet;

public class ChatroomVo extends BaseVo {

	public String id;
	public String name;
	private HashSet<String> users = new HashSet<String>();
	private int type;
	private String userid;
	private String friendid;
	private Integer top = 0;
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashSet<String> getUsers() {
		return users;
	}

	public void setUsers(HashSet<String> users) {
		this.users = users;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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

	public Integer getTop() {
		return top;
	}

	public void setTop(Integer top) {
		this.top = top;
	}
}
