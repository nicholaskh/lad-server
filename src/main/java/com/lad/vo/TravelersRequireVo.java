package com.lad.vo;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class TravelersRequireVo extends BaseVo {
	private String destination;	// 目的地
	private String times;		// 出发时段
	private String type;		// 旅行方式
	private String sex;			// 驴友性别
	private String age;			// 驴友年龄
	private String createuid;
	private int expired;		// 是否过期
	private List<String> images;	
}
