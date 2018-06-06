package com.lad.bo;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Document(collection = "travelersRequire")
public class TravelersRequireBo extends BaseBo {
	private String destination;	// 目的地
	private String days;		// 天数
	private String type;		// 旅行方式
	private String sex;			// 驴友性别
	private String age;			// 驴友年龄
	private Object assembleTime;// 集合时间
	private String assemblePlace;// 集合地点
	private List<String> images;//图片
}
