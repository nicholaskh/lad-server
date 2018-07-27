package com.lad.bo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
@Document(collection = "waiters")
public class WaiterBo extends BaseBo{

	private String nickName;	// 昵称
	private int sex;		// 性别 0,女;1,男
	private Object birthday;	// 生日
	private int age;		// 年龄,后台计算
	private int hight;		// 身高
	private int education; 	// 学历 0,不限;1,初中;2,高中;3,大专;4,本科;5,研究生及以上
	private int marriaged;	// 婚史 0,没有;1,爱过;-1,不限
	private String job;			// 职业
	private String salary;		// 收入
	private String nowin;		// 居住地
	private List<String> images = new ArrayList<>();// 照片地址,保存url的list;
	private Map<String,Set<String>> hobbys = new HashMap<>();// 兴趣,list
	private Map<String,Set<String>> cares = new  HashMap<String,Set<String>>(); // 感兴趣的人,保存id的list;
	private Set<String> pass = new HashSet<>();	// 不感兴趣的人,保存id的list;
}
