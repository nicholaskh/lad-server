package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedList;

@Document(collection = "homepage")
public class HomepageBo extends BaseBo {

	private static final long serialVersionUID = 1L;
	private String owner_id;
	private int new_visitors_count;
	private int total_visitors_count;
	private LinkedList<String> visitor_ids = new LinkedList<String>();

	public String getOwner_id() {
		return owner_id;
	}

	public void setOwner_id(String owner_id) {
		this.owner_id = owner_id;
	}

	public Integer getNew_visitors_count() {
		return new_visitors_count;
	}

	public void setNew_visitors_count(Integer new_visitors_count) {
		this.new_visitors_count = new_visitors_count;
	}

	public Integer getTotal_visitors_count() {
		return total_visitors_count;
	}

	public void setTotal_visitors_count(Integer total_visitors_count) {
		this.total_visitors_count = total_visitors_count;
	}

	public LinkedList<String> getVisitor_ids() {
		return visitor_ids;
	}

	public void setVisitor_ids(LinkedList<String> visitor_ids) {
		this.visitor_ids = visitor_ids;
	}

}
