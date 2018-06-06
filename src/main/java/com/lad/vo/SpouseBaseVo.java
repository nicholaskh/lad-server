package com.lad.vo;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class SpouseBaseVo extends BaseVo{
	private String nickName;
	private String sex;
	private String birthday;
	private Integer marriaged;	// 0,没有;1,爱过;-1,不限
	private String salary;		// 收入
	private String address;		// 居住地
	private List<String> images;// 照片地址
	private List<String> hobbys;// 兴趣,list
}