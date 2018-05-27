package com.lad.bo;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Document(collection = "requires")
public class RequireBo extends BaseBo{
	private Integer sex;		// 性别 0,女;1,男
	private String age;			// 年龄要求(范围值)
	private String hight;		// 身高要求(范围值)
	private Integer education; 	// 学历 0,不限;
	private Integer marriaged;	// 婚史 0,没有;1,爱过;-1,不限
	private List<String> job;	// 职业
	private String salary;		// 收入要求(范围值)
	private String nowin;		// 居住地
	private List<String> hobbys;// 兴趣,list
	private String waiterId;
}
