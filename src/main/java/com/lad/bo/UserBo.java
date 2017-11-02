package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

@Document(collection = "user")
public class UserBo extends BaseBo {

	private String userName;

	private String phone;

	private String sex;

	private String password;

	private String headPictureName;

	private String birthDay;

	private String personalizedSignature;

	private List<String> friends = new LinkedList<String>();

	private HashSet<String> chatrooms = new HashSet<String>();

	private List<String> circleTops = new LinkedList<>();

	private int level = 1;

	/**
	 * 面对面群聊
	 */
	private HashSet<String> faceChatrooms = new HashSet<String>();

	private LinkedList<String> chatroomsTop = new LinkedList<String>();

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

	public LinkedList<String> getChatroomsTop() {
		return chatroomsTop;
	}

	public void setChatroomsTop(LinkedList<String> chatroomsTop) {
		this.chatroomsTop = chatroomsTop;
	}

	public HashSet<String> getFaceChatrooms() {
		return faceChatrooms;
	}

	public void setFaceChatrooms(HashSet<String> faceChatrooms) {
		this.faceChatrooms = faceChatrooms;
	}

	public List<String> getCircleTops() {
		return circleTops;
	}

	public void setCircleTops(List<String> circleTops) {
		this.circleTops = circleTops;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
}
