package com.lad.vo;

import java.util.HashSet;
import java.util.LinkedList;

public class UserVo extends UserBaseVo {



	private HashSet<String> chatrooms;
	
	private LinkedList<String> chatroomsTop;



	public HashSet<String> getChatrooms() {
		return chatrooms;
	}

	public void setChatrooms(HashSet<String> chatrooms) {
		this.chatrooms = chatrooms;
	}

	public LinkedList<String> getChatroomsTop() {
		return chatroomsTop;
	}

	public void setChatroomsTop(LinkedList<String> chatroomsTop) {
		this.chatroomsTop = chatroomsTop;
	}

}
