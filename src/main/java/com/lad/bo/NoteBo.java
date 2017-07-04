package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;

@Document(collection = "note")
public class NoteBo extends BaseBo {
	private String subject;
	private String content;
	private HashSet<String> photos = new HashSet<>();
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

	//用于聚合查询的临时值，不用于数据保存
	private long temp;

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

	public HashSet<String> getPhotos() {
		return photos;
	}

	public void setPhotos(HashSet<String> photos) {
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
}
