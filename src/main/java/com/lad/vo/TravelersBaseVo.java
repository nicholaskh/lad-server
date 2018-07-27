package com.lad.vo;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class TravelersBaseVo extends BaseVo {
	private String sex; // 性别 0,女;1,男
	private String birthday; // 生日
	private String address; // 居住地
	private List<String> images;// 照片地址,保存url的list;
	private List<String> hobbys;// 兴趣,list
}
