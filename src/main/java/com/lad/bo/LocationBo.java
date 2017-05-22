package com.lad.bo;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "location")
public class LocationBo extends BaseBo {

	private String userid;
	private double[] position = new double[2];
	
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public double[] getPosition() {
		return position;
	}

	public void setPosition(double[] position) {
		this.position = position;
	}
	
	public LocationBo(){}

	public LocationBo(String userid, double x, double py) {
		this.userid = userid;
		this.position[0] = x;
		this.position[1] = py;
	}
	@PersistenceConstructor
	public LocationBo(String userid , double [] position){
		this.userid = userid;
		this.position = position;
	}

}