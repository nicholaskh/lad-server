package com.lad.vo;


public class UserVoFriends extends UserBaseVo {

	private String friendsTableId;

	private int apply;

	//关联账号的角色，true 表示当前主用户角色是父母，false表示当前主用户角色是子女
	private boolean parent;
	//关联账号状态， 1 表示发送关联申请，2 表示被申请用户的状态， 3表示已建立关联， -1  表示拒绝或取消
	private int relateStatus;

	private String backname;

	public String getFriendsTableId() {
		return friendsTableId;
	}

	public void setFriendsTableId(String friendsTableId) {
		this.friendsTableId = friendsTableId;
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

	public String getBackname() {
		return backname;
	}

	public void setBackname(String backname) {
		this.backname = backname;
	}
}
