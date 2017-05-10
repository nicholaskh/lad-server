package com.lad.bo;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "friends")
public class FriendsBo extends BaseBo {

	private String userid;
	private String friendid;
	private String backName;
	private List tag;
	private String phone;
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

	public String getBackName() {
		return backName;
	}

	public void setBackName(String backName) {
		this.backName = backName;
	}

	public List getTag() {
		return tag;
	}

	public void setTag(List tag) {
		this.tag = tag;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
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

}
