package com.lad.bo;

import java.util.Date;
import java.util.List;

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
	private Integer sex;
	private Object birthday;
	private Integer age;
	private Integer marriaged;	// 0,没有;1,爱过;-1,不限
	private String salary;		// 收入
	private String address;		// 居住地
	private List<String> images;// 照片地址
	private List<String> hobbys;// 兴趣,list
	private List<String> care;// 感兴趣的人,保存id;
	private List<String> pass;	// 不感兴趣的人
}
