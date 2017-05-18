package com.lad.bo;

import com.mongodb.client.model.geojson.Position;

public class PointBo extends BaseBo {
	private String type = "Point";
	private Position coordinate;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Position getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(Position coordinate) {
		this.coordinate = coordinate;
	}

}
