package com.lad.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;

@Getter
@Setter
public class CircleVo extends BaseVo {
	private String id;
	private double[] position;
	private String name;
	private String tag;
	private String sub_tag;
	private String headPicture;
	private String createuid;
	private HashSet<String> users = new HashSet<String>();
	private HashSet<String> usersApply = new HashSet<String>();
	private HashSet<String> usersRefuse = new HashSet<String>();
	private HashSet<String> notes = new HashSet<String>();
	private long notesSize;
	private int usersSize;
	//圈子是否加入
	private boolean isOpen;
	//圈子加入是否需要校验
	private boolean isVerify;
	//圈子公告标题
	private String noticeTitle;
	//圈子公告
	private String notice;

	// 置顶标识，1置顶
	private int top = 0;

	private String description;

	private long visitNum;

	private int userAdd;

	private int unReadNum;

	//省市区 ，直辖市 省市一样
	private String province;
	private String city;
	private String district;

}
