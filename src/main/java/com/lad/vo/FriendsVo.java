package com.lad.vo;

import java.util.HashSet;
import java.util.List;

public class FriendsVo extends BaseVo {
	private String id;
	private String userid;
	private String friendid;
	private String backname;
	private List<String> tag;
	private HashSet<String> phone = new HashSet<>();
	private String description;
	private Integer VIP;
	private Integer black;
	private String username;
	private String picture;
	private String sex;

	private String channelId;

	private int apply;

	//关联账号的角色，true 表示当前主用户角色是父母，false表示当前主用户角色是子女
	private boolean parent;
	//关联账号状态，0表示普通好友， 1 表示发送关联申请，2 表示被申请用户的状态， 3表示已建立关联， -1  表示拒绝或取消
	private int relateStatus;

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

	public String getBackname() {
		return backname;
	}

	public void setBackname(String backname) {
		this.backname = backname;
	}

	public List<String> getTag() {
		return tag;
	}

	public void setTag(List<String> tag) {
		this.tag = tag;
	}

	public HashSet<String> getPhone() {
		return phone;
	}

	public void setPhone(HashSet<String> phone) {
		this.phone = phone;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getVIP() {
		return VIP;
	}

	public void setVIP(Integer vIP) {
		VIP = vIP;
	}

	public Integer getBlack() {
		return black;
	}

	public void setBlack(Integer black) {
		this.black = black;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public int getApply() {
		return apply;
	}

	public void setApply(int apply) {
		this.apply = apply;
	}

	public boolean isParent() {
		return parent;
	}

	public void setParent(boolean parent) {
		this.parent = parent;
	}

	public int getRelateStatus() {
		return relateStatus;
	}

	public void setRelateStatus(int relateStatus) {
		this.relateStatus = relateStatus;
	}
}
