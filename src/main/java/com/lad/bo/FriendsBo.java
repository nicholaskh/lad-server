package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;

@Document(collection = "friends")
public class FriendsBo extends BaseBo {

	private String userid;
	private String friendid;
	private int apply;//0：申请；1：同意好友；-1拒绝好友
	private String backname;
	private HashSet<String> phone = new HashSet<>();
	private String description;
	private Integer VIP = 0;
	private Integer black = 0;

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

	public int getApply() {
		return apply;
	}

	public void setApply(int apply) {
		this.apply = apply;
	}

}
