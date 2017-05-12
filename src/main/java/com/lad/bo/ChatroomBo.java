package com.lad.bo;

import java.util.HashSet;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chatroom")
public class ChatroomBo extends BaseBo {

	private String name;

	private HashSet<String> users;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashSet<String> getUsers() {
		return users;
	}

	public void setUsers(HashSet<String> users) {
		this.users = users;
	}

}
