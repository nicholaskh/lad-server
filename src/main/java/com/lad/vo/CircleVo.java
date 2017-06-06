package com.lad.vo;

import java.util.HashSet;

public class CircleVo extends BaseVo {
	private String id;
	private double[] position;
	private String landmark;
	private String name;
	private String tag;
	private String sub_tag;
	private String category;
	private String headPicture;
	private HashSet<String> users = new HashSet<String>();
	private HashSet<String> usersApply = new HashSet<String>();
	private HashSet<String> usersRefuse = new HashSet<String>();
	private HashSet<String> organizations = new HashSet<String>();
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public double[] getPosition() {
		return position;
	}
	public void setPosition(double[] position) {
		this.position = position;
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
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getHeadPicture() {
		return headPicture;
	}
	public void setHeadPicture(String headPicture) {
		this.headPicture = headPicture;
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
	public HashSet<String> getUsersRefuse() {
		return usersRefuse;
	}
	public void setUsersRefuse(HashSet<String> usersRefuse) {
		this.usersRefuse = usersRefuse;
	}
	public HashSet<String> getOrganizations() {
		return organizations;
	}
	public void setOrganizations(HashSet<String> organizations) {
		this.organizations = organizations;
	}
}
