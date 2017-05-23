package com.lad.vo;

import java.util.HashSet;
import java.util.LinkedHashSet;

public class UserVo extends BaseVo {
	private String id;

	private String userName;

	private String phone;

	private String sex;

	private String headPictureName;

	private String birthDay;

	private String personalizedSignature;

	private HashSet<String> chatrooms;
	
	private LinkedHashSet<String> chatroomsTop;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getHeadPictureName() {
		return headPictureName;
	}

	public void setHeadPictureName(String headPictureName) {
		this.headPictureName = headPictureName;
	}

	public String getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(String birthDay) {
		this.birthDay = birthDay;
	}

	public String getPersonalizedSignature() {
		return personalizedSignature;
	}

	public void setPersonalizedSignature(String personalizedSignature) {
		this.personalizedSignature = personalizedSignature;
	}

	public HashSet<String> getChatrooms() {
		return chatrooms;
	}

	public void setChatrooms(HashSet<String> chatrooms) {
		this.chatrooms = chatrooms;
	}

	public LinkedHashSet<String> getChatroomsTop() {
		return chatroomsTop;
	}

	public void setChatroomsTop(LinkedHashSet<String> chatroomsTop) {
		this.chatroomsTop = chatroomsTop;
	}

}
