package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedList;

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

	//总数
	private long temp;

	//是否同步个人动态
	private boolean isAsync;

	//0 原创 ， 1转发
	private int forward;
	//转发的原帖子id
	private String sourceid;
	//0 表示帖子， 1表示资讯
	private int noteType;
	//资讯类型
	private int inforType;
	//来源资讯类型名称
	private String inforTypeName;
	//帖子中@的用户
	private LinkedList<String> atUsers;

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public LinkedList<String> getPhotos() {
		return photos;
	}

	public void setPhotos(LinkedList<String> photos) {
		this.photos = photos;
	}

	public String getLandmark() {
		return landmark;
	}

	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}

	public double[] getPosition() {
		return position;
	}

	public void setPosition(double[] position) {
		this.position = position;
	}

	public String getCircleId() {
		return circleId;
	}

	public void setCircleId(String circleId) {
		this.circleId = circleId;
	}

	public long getVisitcount() {
		return visitcount;
	}

	public void setVisitcount(long visitcount) {
		this.visitcount = visitcount;
	}

	public long getTranscount() {
		return transcount;
	}

	public void setTranscount(long transcount) {
		this.transcount = transcount;
	}

	public long getCommentcount() {
		return commentcount;
	}

	public void setCommentcount(long commentcount) {
		this.commentcount = commentcount;
	}

	public long getThumpsubcount() {
		return thumpsubcount;
	}

	public void setThumpsubcount(long thumpsubcount) {
		this.thumpsubcount = thumpsubcount;
	}

	public long getTemp() {
		return temp;
	}

	public void setTemp(long temp) {
		this.temp = temp;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

    public int getEssence() {
        return essence;
    }

    public void setEssence(int essence) {
        this.essence = essence;
    }

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public String getVideoPic() {
		return videoPic;
	}

	public void setVideoPic(String videoPic) {
		this.videoPic = videoPic;
	}

	public boolean isAsync() {
		return isAsync;
	}

	public void setAsync(boolean async) {
		isAsync = async;
	}

	public long getCollectcount() {
		return collectcount;
	}

	public void setCollectcount(long collectcount) {
		this.collectcount = collectcount;
	}

	public int getForward() {
		return forward;
	}

	public void setForward(int forward) {
		this.forward = forward;
	}

	public String getSourceid() {
		return sourceid;
	}

	public void setSourceid(String sourceid) {
		this.sourceid = sourceid;
	}

	public int getNoteType() {
		return noteType;
	}

	public void setNoteType(int noteType) {
		this.noteType = noteType;
	}

	public int getInforType() {
		return inforType;
	}

	public void setInforType(int inforType) {
		this.inforType = inforType;
	}

	public String getInforTypeName() {
		return inforTypeName;
	}

	public void setInforTypeName(String inforTypeName) {
		this.inforTypeName = inforTypeName;
	}

	public LinkedList<String> getAtUsers() {
		return atUsers;
	}

	public void setAtUsers(LinkedList<String> atUsers) {
		this.atUsers = atUsers;
	}
}
