package com.lad.vo;

import java.util.HashSet;
import java.util.LinkedHashSet;

public class UserVo extends UserBaseVo {



	private HashSet<String> chatrooms;
	
	private LinkedHashSet<String> chatroomsTop;



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
