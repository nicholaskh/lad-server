package com.lad.bo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedHashSet;
import java.util.LinkedList;

@Getter
@Setter
@Document(collection = "note")
public class NoteBo extends BaseBo {
	private String subject;
	private String content;
	private LinkedList<String> photos = new LinkedList<>();
	private String landmark;
	private double[] position;
	private String circleId;
	//访问量
	private long visitcount;
	//转发量
	private long transcount;
	//评论数
	private long commentcount;
	//点赞数
	private long thumpsubcount;
	//点赞数
	private long collectcount;

	//精华  管理员操作
	private int essence;
	//置顶  管理员操作
	private int top;

	//上传的文件类型，前端传值
	private String type;

	//视频缩略图
	private String videoPic;

	//热门数
	private double temp;

	//是否同步个人动态
	private boolean isAsync;

	//0 原创 ， 1转发
	private int forward;
	//转发的原帖子id
	private String sourceid;
	//帖子中@的用户
	private LinkedList<String> atUsers;

	//0 表示帖子， 1表示资讯
	private int noteType;
	//资讯类型
	private int inforType;
	//来源资讯类型名称
	private String inforTypeName;
	
	private String forwardUsers;

	//转发 时 前面涉及的所有noteid
	private LinkedHashSet<String> preNoteids;
	//转发时的评论
	private String view;

	//发布日期
	private String createDate;

}
