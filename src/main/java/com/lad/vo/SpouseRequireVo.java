package com.lad.vo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class SpouseRequireVo extends BaseVo{
	private String age;			// 年龄要求(范围值)
	private String salary;		// 收入要求(范围值)
	private String address;		// 居住地
	private String sex;
	private Map<String,Set<String>> hobbys = new HashMap<>();// 兴趣,list
}