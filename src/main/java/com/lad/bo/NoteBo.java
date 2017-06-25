package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "note")
public class NoteBo extends BaseBo {
	private String subject;
	private String content;
	private String photo;
	private String landmark;
	private double[] position;
	private String circleId;
	//访问量
	private long visitcount;
	//转发量
	private long transcount;
	//评论数
	private long commentcount;

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

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
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
}
