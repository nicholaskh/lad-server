package com.lad.bo;

import java.util.HashSet;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "circle")
public class CircleBo extends BaseBo {
	private double[] position;
	//地标
	private String landmark;
	private String name;
	private String tag;
	private String sub_tag;
	//类别
	private String category;
	private String headPicture;
	private HashSet<String> users = new HashSet<String>();
	private HashSet<String> usersApply = new HashSet<String>();
	private HashSet<String> usersRefuse = new HashSet<String>();
	//群组
	private HashSet<String> organizations = new HashSet<String>();
	//帖子
	private HashSet<String> notes = new HashSet<String>();

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

	public HashSet<String> getOrganizations() {
		return organizations;
	}

	public void setOrganizations(HashSet<String> organizations) {
		this.organizations = organizations;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public HashSet<String> getUsersRefuse() {
		return usersRefuse;
	}

	public void setUsersRefuse(HashSet<String> usersRefuse) {
		this.usersRefuse = usersRefuse;
	}

	public String getHeadPicture() {
		return headPicture;
	}

	public void setHeadPicture(String headPicture) {
		this.headPicture = headPicture;
	}

	public HashSet<String> getNotes() {
		return notes;
	}

	public void setNotes(HashSet<String> notes) {
		this.notes = notes;
	}
}
