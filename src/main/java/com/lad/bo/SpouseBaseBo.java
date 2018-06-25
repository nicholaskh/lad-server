package com.lad.bo;

import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Document(collection = "spousebase")
public class SpouseBaseBo extends BaseBo {
	private String nickName;
	private String sex;
	private Object birthday;
	private int age;
	private int marriaged;	// 0,没有;1,爱过;-1,不限
	private String salary;		// 收入
	private String address;		// 居住地
	private List<String> images;// 照片地址
	private List<String> hobbys;// 兴趣,list
	private Map<String,List> care;// 感兴趣的人,保存id;
	private List<String> pass;	// 不感兴趣的人
}
