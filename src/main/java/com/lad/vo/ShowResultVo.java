package com.lad.vo;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ShowResultVo {
	private String nickName;
	private String headPicture;
	private List images;
	private Object hobbys;
	private String sex;
	private Object age;
	
	// 驴友
	private String days;
	private String destination;
	private String type;
	private String address;
	private String require;
	private String birthday;
}
