package com.lad.util;

public enum ERRORCODE {
	ACCOUNT_NAME(10001, "用户名错误"),
	ACCOUNT_PASSWORD(10002, "密码错误"),
	ACCOUNT_PHONE_REPEAT(10003, "手机号码重复"),
	ACCOUNT_PHONE_ERROR(10004, "手机号码错误"),
	ACCOUNT_OFF_LINE(10005, "未登录"),
	ACCOUNT_NULL_BIRTHDAY(10005, "未登录"),
	ACCOUNT_ID(10006, "用户名ID错误"),
	ACCOUNT_PHONE_NULL(10007, "手机号码为空"),

	USER_USERNAME(20001, "用户名错误"),
	USER_SEX(20002, "性别错误"),
	USER_SIGNATURE(20003, "个性签名错误"),
	USER_BIRTHDAY(20004, "生日错误"),
	USER_PHONE(20005, "手机号码错误"),
	USER_ID(20006, "用户ID错误"),
	USER_NULL(20007, "用户为空"),
	
	SECURITY_PASSWORD_INCONSISTENCY(30001, "密码不一致"),
	SECURITY_WRONG_VERIFICATION(30003, "验证码错误"),

	CONTACT_VISITOR(40001, "访问者ID错误"),
	CONTACT_HOMEPAGE(40002, "首页为空"),
	CONTACT_SOURCE(40003, "消息来源错误"),
	CONTACT_CONTENT(40004, "消息内容错误"),
	CONTACT_THUMBSUP_DUPLICATE(40005, "重复点赞"),
	
	FEEDBACK_NULL(50001, "反馈为空"),
	
	CHATROOM_NAME_NULL(60001, "聊天室name为空"),
	CHATROOM_ID_NULL(60002, "聊天室ID为空"),
	CHATROOM_NULL(60003, "聊天室为空"),
	CHATROOM_EXIST(60004, "已经加入该聊天室"),
	CHATROOM_TOP_EXIST(60005, "该聊天室已经置顶"),
	CHATROOM_SEQ_EXPIRE(60009, "聊天室seq过期"),
	
	PUSHED_ERROR(70002, "PUSHED系统错误"),
	PUSHED_CONNECT_ERROR(70003, "PUSHED系统连接错误"),
	
	FRIEND_NULL(80001, "朋友为空"),
	FRIEND_VIP_NULL(80002, "VIP为空"),
	FRIEND_BLACK_NULL(80003, "黑名单为空"),
	FRIEND_EXIST(80004, "朋友关系已经存在"),
	FRIEND_TAG_NULL(80005, "朋友标签为空"),
	FRIEND_PHONE_NULL(80006, "朋友电话为空"),
	FRIEND_DESCRIPTION_NULL(80007, "朋友描述为空"),
	FRIEND_BACKNAME_NULL(80008, "备注为空"),
	
	COMPLAIN_IS_NULL(90001, "投诉为空");
	
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
