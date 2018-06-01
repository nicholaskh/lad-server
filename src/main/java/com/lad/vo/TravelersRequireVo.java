package com.lad.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class TravelersRequireVo extends BaseVo {
	private String destination;	// 目的地
	private String days;		// 天数
	private String type;		// 旅行方式
	private String sex;			// 驴友性别
	private String age;			// 驴友年龄
	private String assembleTime;// 集合时间
	private String assemblePlace;// 集合地点
}
