package com.junlenet.mongodb.demo.util;

public enum ERRORCODE {
	ACCOUNT_NAME(10001, "用户名错误"),
	ACCOUNT_PASSWORD(10002, "密码错误"),
	ACCOUNT_PHONE_RPEAT(10003, "手机号码重复"),
	ACCOUNT_PHONE_ERROR(10004, "手机号码错误"),
	ACCOUNT_OFF_LINE(10004, "未登录"),

	USER_USERNAME(20001, "用户名错误"),
	USER_SEX(20002, "性别错误"),
	USER_SIGNATURE(20003, "个性签名错误"),
	
	SECURITY_PASSWORD_INCONSISTENCY(30001, "密码不一致"),

	CONTACT_VISITOR(40001, "访问者ID错误"),

	FEEDBACK_SOURCE(50001, "消息来源错误"),
	FEEDBACK_CONTENT(50002, "消息内容错误");

	private int index;
	private String reason;
	
	private ERRORCODE(int index, String reason){
		this.index = index;
		this.reason = reason;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

}
