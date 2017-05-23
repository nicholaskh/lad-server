package com.lad.bo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user")
public class UserBo extends BaseBo implements Serializable {

	private static final long serialVersionUID = 2928923917001675021L;

	private String userName;

	private String phone;

	private String sex;

	private String password;

	private String headPictureName;

	private String birthDay;

	private String personalizedSignature;

	private List<String> friends;

	private HashSet<String> chatrooms;
	
	private LinkedHashSet<String> chatroomsTop;

	private String locationid;

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public List<String> getFriends() {
		return friends;
	}

	public void setFriends(List<String> friends) {
		this.friends = friends;
	}

	public HashSet<String> getChatrooms() {
		return chatrooms;
	}

	public void setChatrooms(HashSet<String> chatrooms) {
		this.chatrooms = chatrooms;
	}

	public String getLocationid() {
		return locationid;
	}

	public void setLocationid(String locationid) {
		this.locationid = locationid;
	}

	public LinkedHashSet<String> getChatroomsTop() {
		return chatroomsTop;
	}

	public void setChatroomsTop(LinkedHashSet<String> chatroomsTop) {
		this.chatroomsTop = chatroomsTop;
	}
}
