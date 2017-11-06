package com.lad.vo;

import java.util.LinkedHashSet;

public class ChatroomVo extends BaseVo {

	public String id;
	public String name;

	private int type;
	private String userid;
	private String friendid;
	private Integer top = 0;

	private String description;
	//是否允许加入
	private boolean isOpen;
	//圈子加入是否需要校验
	private boolean isVerify;

	private int userNum = 1;

	//是否开启免打扰模式 true 是； false 否
	private boolean isDisturb;

	//是否显示昵称  true 是； false 否
	private boolean isShowNick;

	private LinkedHashSet<ChatroomUserVo> userVos = new LinkedHashSet<>();


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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean open) {
		isOpen = open;
	}

	public boolean isVerify() {
		return isVerify;
	}

	public void setVerify(boolean verify) {
		isVerify = verify;
	}

	public int getUserNum() {
		return userNum;
	}

	public void setUserNum(int userNum) {
		this.userNum = userNum;
	}

	public LinkedHashSet<ChatroomUserVo> getUserVos() {
		return userVos;
	}

	public void setUserVos(LinkedHashSet<ChatroomUserVo> userVos) {
		this.userVos = userVos;
	}

	public boolean isDisturb() {
		return isDisturb;
	}

	public void setDisturb(boolean disturb) {
		isDisturb = disturb;
	}

	public boolean isShowNick() {
		return isShowNick;
	}

	public void setShowNick(boolean showNick) {
		isShowNick = showNick;
	}
}
