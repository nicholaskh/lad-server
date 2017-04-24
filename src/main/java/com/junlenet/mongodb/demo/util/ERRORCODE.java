package com.junlenet.mongodb.demo.util;

public enum ERRORCODE {
	ACCOUNT_NAME(10001, "用户名错误"),
	ACCOUNT_PASSWORD(10002, "密码错误"),
	ACCOUNT_PHONE_REPEAT(10003, "手机号码重复"),
	ACCOUNT_PHONE_ERROR(10004, "手机号码错误"),
	ACCOUNT_OFF_LINE(10005, "未登录"),
	ACCOUNT_NULL_BIRTHDAY(10005, "未登录"),

	USER_USERNAME(20001, "用户名错误"),
	USER_SEX(20002, "性别错误"),
	USER_SIGNATURE(20003, "个性签名错误"),
	USER_BIRTHDAY(20004, "生日错误"),
	
	SECURITY_PASSWORD_INCONSISTENCY(30001, "密码不一致"),
	SECURITY_WRONG_VERIFICATION(30003, "验证码错误"),

	CONTACT_VISITOR(40001, "访问者ID错误"),
	CONTACT_HOMEPAGE(40002, "首页为空"),
	CONTACT_SOURCE(40003, "消息来源错误"),
	CONTACT_CONTENT(40004, "消息内容错误"),
	
	FEEDBACK_NULL(50001, "反馈为空");

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
