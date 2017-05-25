package com.lad.vo;

import java.util.HashSet;

public class OrganizationVo extends BaseVo {
	private String id;
	private String landmark;
	private String name;
	private String tag;
	private String sub_tag;
	private HashSet<String> masters = new HashSet<String>();
	private HashSet<String> users = new HashSet<String>();
	private HashSet<String> usersApply = new HashSet<String>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLandmark() {
		return landmark;
	}

	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getSub_tag() {
		return sub_tag;
	}

	public void setSub_tag(String sub_tag) {
		this.sub_tag = sub_tag;
	}

	public HashSet<String> getMasters() {
		return masters;
	}

	public void setMasters(HashSet<String> masters) {
		this.masters = masters;
	}

	public HashSet<String> getUsers() {
		return users;
	}

	public void setUsers(HashSet<String> users) {
		this.users = users;
	}

	public HashSet<String> getUsersApply() {
		return usersApply;
	}

	public void setUsersApply(HashSet<String> usersApply) {
		this.usersApply = usersApply;
	}
}
