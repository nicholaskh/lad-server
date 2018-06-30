package com.lad.bo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	private List<String> images = new ArrayList<>();// 照片地址,保存url的list;
	private Map<String,Set<String>> hobbys = new HashMap<>();// 兴趣,list
}
