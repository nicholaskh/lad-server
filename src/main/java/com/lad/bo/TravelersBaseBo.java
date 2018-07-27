package com.lad.bo;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Document(collection="travelersBase")
public class TravelersBaseBo extends BaseBo {
	private String nickName;	// 昵称
	private String sex;		// 性别 0,女;1,男
	private Date birthday;		// 生日
	private int age;		// 年龄,后台计算
	private String address;		// 居住地
	private List<String> images;// 照片地址,保存url的list;
	private List<String> hobbys;// 兴趣,list
	private Map<String,List> care; // 感兴趣的人,保存id的list;
	private List<String> pass;	// 不感兴趣的人,保存id的list;
}
