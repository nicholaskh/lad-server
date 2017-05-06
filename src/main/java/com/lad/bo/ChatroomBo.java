package com.lad.bo;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chatroom")
public class ChatroomBo extends BaseBo {

	private String name;

	private List<String> users;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getUsers() {
		return users;
	}

	public void setUsers(List<String> users) {
		this.users = users;
	}

}
