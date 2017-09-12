package com.lad.vo;

import java.util.HashSet;

public class CircleVo extends BaseVo {
	private String id;
	private double[] position;
	private String name;
	private String tag;
	private String sub_tag;
	private String headPicture;
	private String createuid;
	private HashSet<String> users = new HashSet<String>();
	private HashSet<String> usersApply = new HashSet<String>();
	private HashSet<String> usersRefuse = new HashSet<String>();
	private HashSet<String> notes = new HashSet<String>();
	private int notesSize;
	private int usersSize;
	//圈子是否加入
	private boolean isOpen;
	//圈子加入是否需要校验
	private boolean isVerify;
	//圈子公告标题
	private String noticeTitle;
	//圈子公告
	private String notice;

	// 置顶标识，1置顶
	private int top = 0;

	private String description;

	private int visitNum;

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

	public HashSet<String> getNotes() {
		return notes;
	}

	public void setNotes(HashSet<String> notes) {
		this.notes = notes;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public String getCreateuid() {
		return createuid;
	}

	public int getNotesSize() {
		return notesSize;
	}

	public void setNotesSize(int notesSize) {
		this.notesSize = notesSize;
	}

	public int getUsersSize() {
		return usersSize;
	}

	public void setUsersSize(int usersSize) {
		this.usersSize = usersSize;
	}

	public void setCreateuid(String createuid) {
		this.createuid = createuid;
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

	public String getNoticeTitle() {
		return noticeTitle;
	}

	public void setNoticeTitle(String noticeTitle) {
		this.noticeTitle = noticeTitle;
	}

	public String getNotice() {
		return notice;
	}

	public void setNotice(String notice) {
		this.notice = notice;
	}

	public int getVisitNum() {
		return visitNum;
	}

	public void setVisitNum(int visitNum) {
		this.visitNum = visitNum;
	}
}
