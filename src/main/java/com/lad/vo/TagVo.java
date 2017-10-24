package com.lad.vo;

import java.util.LinkedHashSet;

public class TagVo extends BaseVo {
	private String tagid;

	private String tagName;

	private int userNum;

	private LinkedHashSet<String> userNames = new LinkedHashSet<>();

	public String getTagid() {
		return tagid;
	}

	public void setTagid(String tagid) {
		this.tagid = tagid;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public int getUserNum() {
		return userNum;
	}

	public void setUserNum(int userNum) {
		this.userNum = userNum;
	}

	public LinkedHashSet<String> getUserNames() {
		return userNames;
	}

	public void setUserNames(LinkedHashSet<String> userNames) {
		this.userNames = userNames;
	}
}
