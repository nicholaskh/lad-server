package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 * 删除圈子中的群组，圈子代替群组
 */
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
	private int usernum;

	private LinkedHashSet<String> masters = new LinkedHashSet<>();

	private String description;

	//圈子5公里是否加入
	private boolean isOpen;

	private int noteSize;

	//总人数=圈子内总评论+总阅读+总点赞+总转发
	private int total;

	private HashSet<String> users = new HashSet<String>();
	private HashSet<String> usersApply = new HashSet<String>();
	private HashSet<String> usersRefuse = new HashSet<String>();

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

	public int getUsernum() {
		return usernum;
	}
	public void setUsernum(int usernum) {
		this.usernum = usernum;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LinkedHashSet<String> getMasters() {
		return masters;
	}

	public void setMasters(LinkedHashSet<String> masters) {
		this.masters = masters;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean open) {
		isOpen = open;
	}

	public int getNoteSize() {
		return noteSize;
	}

	public void setNoteSize(int noteSize) {
		this.noteSize = noteSize;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
}
