package com.lad.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 * 删除圈子中的群组，圈子代替群组
 */
@Getter
@Setter
@ToString
@Document(collection = "circle")
public class CircleBo extends BaseBo {
	private double[] position;
	
	private String name;
	//一级分类
	private String tag;
	//二级分类
	private String sub_tag;
	//类别
	private String category;
	// 头像
	private String headPicture;
	// 用户数
	private int usernum;
	//管理员
	private LinkedHashSet<String> masters = new LinkedHashSet<>();
	//描述
	private String description;
	//圈子5公里是否加入
	private boolean isOpen;
	//圈子加入是否需要校验
	private boolean isVerify;
	//帖子数
	private int noteSize;
	//总人数=圈子内总评论+总阅读+总点赞+总转发
	private int total;
	//评论数
	private int commentNum;
	//转发数
	private int transmitNum;
	//访问
	private int visitNum;
	//点赞
	private int thumpNum;
	//圈子公告标题
	private String noticeTitle;
	//圈子公告
	private String notice;
	//圈子公告发布时间
	private Date noticeTime;
	//圈子公告发布人
	private String noticeUserid;
	//圈子总聚会数
	private int partyNum;
	//圈子发帖数
	private int noteNum;
	//聚会访问
	private int partyVisit;
	//聚会点赞
	private int partyThump;
	//聚会分享
	private int partyShare;
	//圈子热度=发帖+活动+阅读+点赞+分享
	private double hotNum;
	//省市区 ，直辖市 省市一样
	private String province;
	private String city;
	private String district;

	//用户
	private HashSet<String> users = new LinkedHashSet<>();
	//申请用户
	private HashSet<String> usersApply = new LinkedHashSet<>();
	//拒绝用户(黑名单)
	private HashSet<String> usersRefuse = new LinkedHashSet<>();

	//圈子是否承接演出
	private boolean takeShow;
}
