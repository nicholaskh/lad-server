package com.lad.bo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Document(collection="spouserequire")
public class SpouseRequireBo extends BaseBo {
	private String sex;		// 性别 隐藏字段
	private String age;			// 年龄要求(范围值)
	private String salary;		// 收入要求(范围值)
	private String address;		// 居住地
	private Map<String,Set<String>> hobbys = new HashMap<>();// 兴趣,list
	private String baseId;
}
