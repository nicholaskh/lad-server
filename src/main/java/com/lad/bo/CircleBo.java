package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 * 删除圈子中的群组，圈子代替群组
 */
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
	private String headPicture;
	private int usernum;
	//管理员
	private LinkedHashSet<String> masters = new LinkedHashSet<>();
	//描述
	private String description;

	//圈子5公里是否加入
	private boolean isOpen;

	//圈子加入是否需要校验
	private boolean isVerify;

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

	private int partyVisit;

	private int partyThump;

	private int partyShare;


	//圈子热度=发帖+活动+阅读+点赞+分享
	private long hotNum;

	//省市区 ，直辖市 省市一样
	private String province;
	private String city;
	private String district;


	private HashSet<String> users = new LinkedHashSet<>();
	private HashSet<String> usersApply = new LinkedHashSet<>();
	private HashSet<String> usersRefuse = new LinkedHashSet<>();

	public double[] getPosition() {
		return position;
	}

	public void setPosition(double[] position) {
		this.position = position;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getSub_tag() {
		return sub_tag;
	}

	public void setSub_tag(String sub_tag) {
		this.sub_tag = sub_tag;
	}

	public HashSet<String> getUsers() {
		return users;
	}

	public void setUsers(HashSet<String> users) {
		this.users = users;
	}

	public HashSet<String> getUsersApply() {
		return usersApply;
	}

	public void setUsersApply(HashSet<String> usersApply) {
		this.usersApply = usersApply;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public HashSet<String> getUsersRefuse() {
		return usersRefuse;
	}

	public void setUsersRefuse(HashSet<String> usersRefuse) {
		this.usersRefuse = usersRefuse;
	}

	public String getHeadPicture() {
		return headPicture;
	}

	public void setHeadPicture(String headPicture) {
		this.headPicture = headPicture;
	}

	public int getUsernum() {
		return usernum;
	}
	public void setUsernum(int usernum) {
		this.usernum = usernum;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LinkedHashSet<String> getMasters() {
		return masters;
	}

	public void setMasters(LinkedHashSet<String> masters) {
		this.masters = masters;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean open) {
		isOpen = open;
	}

	public int getNoteSize() {
		return noteSize;
	}

	public void setNoteSize(int noteSize) {
		this.noteSize = noteSize;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}


	public boolean isVerify() {
		return isVerify;
	}

	public void setVerify(boolean verify) {
		isVerify = verify;
	}

	public String getNoticeTitle() {
		return noticeTitle;
	}

	public void setNoticeTitle(String noticeTitle) {
		this.noticeTitle = noticeTitle;
	}

	public String getNotice() {
		return notice;
	}

	public void setNotice(String notice) {
		this.notice = notice;
	}

	public int getPartyNum() {
		return partyNum;
	}

	public void setPartyNum(int partyNum) {
		this.partyNum = partyNum;
	}

	public int getNoteNum() {
		return noteNum;
	}

	public void setNoteNum(int noteNum) {
		this.noteNum = noteNum;
	}

	public int getCommentNum() {
		return commentNum;
	}

	public void setCommentNum(int commentNum) {
		this.commentNum = commentNum;
	}

	public int getTransmitNum() {
		return transmitNum;
	}

	public void setTransmitNum(int transmitNum) {
		this.transmitNum = transmitNum;
	}

	public int getVisitNum() {
		return visitNum;
	}

	public void setVisitNum(int visitNum) {
		this.visitNum = visitNum;
	}

	public int getThumpNum() {
		return thumpNum;
	}

	public void setThumpNum(int thumpNum) {
		this.thumpNum = thumpNum;
	}

	public long getHotNum() {
		return hotNum;
	}

	public void setHotNum(long hotNum) {
		this.hotNum = hotNum;
	}

	public int getPartyVisit() {
		return partyVisit;
	}

	public void setPartyVisit(int partyVisit) {
		this.partyVisit = partyVisit;
	}

	public int getPartyThump() {
		return partyThump;
	}

	public void setPartyThump(int partyThump) {
		this.partyThump = partyThump;
	}

	public int getPartyShare() {
		return partyShare;
	}

	public void setPartyShare(int partyShare) {
		this.partyShare = partyShare;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public Date getNoticeTime() {
		return noticeTime;
	}

	public void setNoticeTime(Date noticeTime) {
		this.noticeTime = noticeTime;
	}

	public String getNoticeUserid() {
		return noticeUserid;
	}

	public void setNoticeUserid(String noticeUserid) {
		this.noticeUserid = noticeUserid;
	}
}
