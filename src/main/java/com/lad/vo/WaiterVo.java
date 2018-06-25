package com.lad.vo;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString

public class WaiterVo extends BaseVo {

	private String createrId;	// 创建者Id
	private String nickName;	// 昵称
	private int sex;		// 性别 0,女;1,男
	private Date birthday;		// 生日
	private int age;			// 生日
	private int hight;		// 身高
	private int education; 	// 学历 0,不限;1,初中;2,高中;3,大专;4,本科;5,研究生及以上
	private int marriaged;	// 婚史 0,没有;1,爱过;-1,不限
	private String job;			// 职业
	private String salary;		// 收入
	private String nowin;		// 居住地
	private List<String> images;// 照片地址,保存url的list;
	private List<String> hobbys;// 兴趣,list
}
