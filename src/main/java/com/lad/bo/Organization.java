package com.lad.bo;

import java.util.HashSet;

public class Organization extends BaseBo {
	private double []position;
	private String landmark;
	private String name;
	private String firstCategory;
	private String secondCategory;
	private HashSet<UserBo> users;
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
	public String getFirstCategory() {
		return firstCategory;
	}
	public void setFirstCategory(String firstCategory) {
		this.firstCategory = firstCategory;
	}
	public String getSecondCategory() {
		return secondCategory;
	}
	public void setSecondCategory(String secondCategory) {
		this.secondCategory = secondCategory;
	}
	public HashSet<UserBo> getUsers() {
		return users;
	}
	public void setUsers(HashSet<UserBo> users) {
		this.users = users;
	}
}
