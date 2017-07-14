package com.lad.util;

public class Constant {
	public static final String HEAD_PICTURE_PATH = "/opt/apps/lad-server/picture/head/";
	public static final String FEEDBACK_PICTURE_PATH = "/opt/apps/lad-server/picture/feedback/";
	public static final String IMFILE_PATH = "/opt/apps/lad-server/picture/imfile/";
	public static final String CIRCLE_HEAD_PICTURE_PATH = "/opt/apps/lad-server/circle/picture/head/";
	public static final String NOTE_PICTURE_PATH = "/opt/apps/lad-server/note/picture/";
	public static final String QINIU_URL = "http://oojih7o1f.bkt.clouddn.com/";

	public static final String RESPONSE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><response><error>0</error><message></message></response>";

	/**
	 * 通用成功返回
	 */
	public static final String COM_RESP =  "{\"ret\":0}";

	/**
	 * 面对面建群lock
	 */
	public static final String CHAT_LOCK = "chatLock";

	/**
	 * 访问量lock
	 */
	public static final String VISIT_LOCK = "visitLock";

	/**
	 * 点赞量lock
	 */
	public static final String THUMB_LOCK = "thumpLock";

	/**
	 * 评论量lock
	 */
	public static final String COMOMENT_LOCK = "commentLock";

	/**
	 * 转发量lock
	 */
	public static final String TRANS_LOCK = "transLock";

	/**
	 * 申请
	 */
	public static final int ADD_APPLY = 0;
	/**
	 * 申请同意
	 */
	public static final int ADD_AGREE= 1;
	/**
	 * 申请拒绝
	 */
	public static final int ADD_REFUSE= -1;


	/** 
	 * 激活
	 */
	public static final int ACTIVITY= 0;
	/**
	 * 删除
	 */
	public static final int DELETED = 1;

}
