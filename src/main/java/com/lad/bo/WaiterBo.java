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
@Document(collection = "waiters")
public class WaiterBo extends BaseBo{
//	private String id;
//	private String createrId;	// 创建者Id
	private String nickName;	// 昵称
	private Integer sex;		// 性别 0,女;1,男
	private Object birthday;		// 生日
	private Integer age;		// 年龄,后台计算
	private Integer hight;		// 身高
	private Integer education; 	// 学历 0,不限;1,初中;2,高中;3,大专;4,本科;5,研究生及以上
	private Integer marriaged;	// 婚史 0,没有;1,爱过;-1,不限
	private String job;			// 职业
	private String salary;		// 收入
	private String nowin;		// 居住地
	private List<String> images;// 照片地址,保存url的list;
	private List<String> hobbys;// 兴趣,list
	private Map<String,List> cares; // 感兴趣的人,保存id的list;
	private List<String> pass;	// 不感兴趣的人,保存id的list;
//	private String requireId;	// 关联的require的id
//	private Date createDate;	// 创建时间
}
