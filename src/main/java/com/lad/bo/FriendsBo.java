package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;

@Document(collection = "friends")
public class FriendsBo extends BaseBo {
	//主用户
	private String userid;
	//好友用户
	private String friendid;
	private int apply;//0：申请；1：同意好友；-1拒绝好友
	private String backname;
	private HashSet<String> phone = new HashSet<>();
	private String description;
	private Integer VIP = 0;
	private Integer black = 0;
	private String username;
	private String friendHeadPic;
	private String chatroomid;
	//关联账号的角色，true 表示当前主用户角色是父母，false表示当前主用户角色是子女
	private boolean parent;
	//关联账号状态， 1 表示发送关联申请，2 表示被申请用户的状态， 3表示已建立关联， -1  表示拒绝或取消
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFriendHeadPic() {
		return friendHeadPic;
	}

	public void setFriendHeadPic(String friendHeadPic) {
		this.friendHeadPic = friendHeadPic;
	}

	public String getChatroomid() {
		return chatroomid;
	}

	public void setChatroomid(String chatroomid) {
		this.chatroomid = chatroomid;
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
