package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tempFaceToFaceChatroom")
public class TempFaceToFaceChatroomBo extends BaseBo {
	
	private String seq;
	private double[] position;
	private String userid;
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public double[] getPosition() {
		return position;
	}
	public void setPosition(double[] position) {
		this.position = position;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
}
