package com.lad.util;

public enum ERRORCODE {
	ACCOUNT_NAME_PASSWORD(10001, "用户名或密码错误"),
	ACCOUNT_PASSWORD(10002, "密码错误"),
	ACCOUNT_PHONE_REPEAT(10003, "手机号码已存在"),
	ACCOUNT_PHONE_ERROR(10004, "手机号码错误"),
	ACCOUNT_OFF_LINE(10005, "未登录"),
	ACCOUNT_NULL_BIRTHDAY(10005, "用户生日为空"),
	ACCOUNT_ID(10006, "用户名ID错误"),
	ACCOUNT_PHONE_NULL(10007, "手机号码为空"),
	ACCOUNT_PHONE_EXIST(10008, "手机号未注册"),

	USER_USERNAME(20001, "用户名错误"),
	USER_SEX(20002, "性别错误"),
	USER_SIGNATURE(20003, "个性签名错误"),
	USER_BIRTHDAY(20004, "生日错误"),
	USER_PHONE(20005, "手机号码错误"),
	USER_ID(20006, "用户ID错误"),
	USER_NULL(20007, "用户为空"),
	
	SECURITY_PASSWORD_INCONSISTENCY(30001, "密码不一致"),
	SECURITY_VERIFICATION_NULL(30002, "验证码为空"),
	SECURITY_WRONG_VERIFICATION(30003, "验证码错误"),
	SECURITY_VERIFICATION_TIMEOUT(30004, "验证码超时"),

	CONTACT_VISITOR(40001, "访问者ID错误"),
	CONTACT_HOMEPAGE(40002, "首页为空"),
	CONTACT_SOURCE(40003, "消息来源错误"),
	CONTACT_CONTENT(40004, "消息内容错误"),
	CONTACT_THUMBSUP_DUPLICATE(40005, "重复点赞"),
	
	FEEDBACK_NULL(50001, "反馈为空"),
	
	CHATROOM_NAME_NULL(60001, "聊天室name为空"),
	CHATROOM_ID_NULL(60002, "聊天室ID error"),
	CHATROOM_NULL(60003, "聊天室为空"),
	CHATROOM_EXIST(60004, "已经加入该聊天室"),
	CHATROOM_TOP_EXIST(60005, "该聊天室已经置顶"),
	CHATROOM_SEQ_EXPIRE(60009, "聊天室seq过期"),
	CHATROOM_ERROR(60010, "聊天室创建错误"),
	CHATROOM_TIME_OUT(60011, "聊天室已超过10分钟"),
	CHATROOM_RANGE_OUT(60012, "聊天室距离太远"),
	CHATROOM_NOT_OPEN(60013, "聊天室暂未开放"),
	CHATROOM_MASTER_QUIT(60014, "群主不能直接退群"),
	CHATROOM_APPLY_NULL(60015, "聊天室申请为空"),
	
	PUSHED_ERROR(70002, "PUSHED系统错误"),
	PUSHED_CONNECT_ERROR(70003, "PUSHED系统连接错误"),
	
	FRIEND_NULL(80001, "朋友为空"),
	FRIEND_VIP_NULL(80002, "VIP为空"),
	FRIEND_BLACK_NULL(80003, "黑名单为空"),
	FRIEND_EXIST(80004, "朋友关系已经存在"),
	TAG_NULL(80005, "标签不存在"),
	FRIEND_PHONE_NULL(80006, "朋友电话为空"),
	FRIEND_DESCRIPTION_NULL(80007, "朋友描述为空"),
	FRIEND_BACKNAME_NULL(80008, "备注为空"),
	FRIEND_APPLY_EXIST(80009, "请求好友申请已经发送"),
	FRIEND_DATA_ERROR(80010, "好友信息异常，请联系管理员处理"),
	FRIEND_TAG_NULL(80011, "好友标签为空"),
	TAG_NAME_EXIST(80012, "标签名称已存在"),
	FRIEND_ERROR(80013, "好友关系错误"),
	
	COMPLAIN_IS_NULL(90001, "投诉为空"),
	
	CIRCLE_IS_NULL(110001, "该圈子不存在"),
	CIRCLE_APPLY_USER_NULL(110002, "该用户没有申请加入此圈子"),
	CIRCLE_USER_EXIST(110003, "该用户已经加入此圈子"),
	CIRCLE_MASTER_NULL(110004, "没有权限"),
	CIRCLE_IS_SELF(110005, "群主不能转给自己"),
	CIRCLE_NOT_MASTER(110006, "没有权限"),
	CIRCLE_USER_MAX(110007, "圈子人数已到达上限"),
	CIRCLE_CREATE_MAX(110008, "圈子创建数已到达上限"),
	CIRCLE_USER_NULL(110009, "该用户不在此圈子"),
	CIRCLE_TOP_EXIST(110010, "圈子已经置顶"),
	CIRCLE_NOT_QUIT(110011, "群主不能直接退出群"),
	CIRCLE_TYPE_EXIST(110012, "分类已经存在"),
	CIRCLE_MASTER_MAX(110013, "管理员人数已达上限"),
	CIRCLE_NEED_VERIFY(110014, "加入圈子需要申请"),
	CIRCLE_NAME_EXIST(110015, "圈子名称已经存在"),

	INFOR_IS_NULL(130001, "资讯不存在"),
	INFOR_NAME_ERROR(130002, "分类名称错误"),

	PARTY_ERROR(140001, "聚会参数错误"),
	PARTY_NULL(140002, "聚会不存在"),
	PARTY_TALK_EXIST(140003, "聚会群聊已经存在"),
	PARTY_USER_MAX(140004, "聚会人数已达上限"),
	PARTY_HAS_COMMENT(140005, "该聚会已经评论"),
	PARTY_HAS_ADD(140006, "已经报名聚会"),
	PARTY_NO_AUTH(140007, "没有权限"),
	PARTY_USER_NULL(140008, "聚会报名用户不存在"),
	PARTY_HAS_END(140009, "聚会已经结束"),
	PARTY_NOTICE_NULL(140010, "聚会通知不存在"),

	COLLECT_IS_NULL(150001, "收藏不存在"),
	COLLECT_TYPE_ERR(150002, "收藏类型错误"),
	COLLECT_EXIST(150003, "收藏已存在"),
	
	NOTE_IS_NULL(120001, "帖子不存在"),
	NOTE_NOT_MASTER(120002, "没有权限"),
	FORMAT_ERROR(120005, "格式错误"),
	TYPE_ERROR(120004, "类型错误"),
	COMMENT_IS_NULL(120003, "评论不存在");




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
