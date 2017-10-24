package com.lad.util;

public class Constant {
	public static final String HEAD_PICTURE_PATH = "/opt/apps/lad-server/picture/head/";
	public static final String FEEDBACK_PICTURE_PATH = "/opt/apps/lad-server/picture/feedback/";
	public static final String IMFILE_PATH = "/opt/apps/lad-server/picture/imfile/";
	public static final String CIRCLE_HEAD_PICTURE_PATH = "/opt/apps/lad-server/picture/circle/head/";
	public static final String NOTE_PICTURE_PATH = "/opt/apps/lad-server/picture/note/";
	public static final String DYNAMIC_PICTURE_PATH = "/opt/apps/lad-server/picture/dynamic/";
	public static final String PARTY_PICTURE_PATH = "/opt/apps/lad-server/picture/party/";
	public static final String INFOR_PICTURE_PATH = "/opt/apps/lad-server/picture/infor/";
	public static final String QINIU_URL = "http://oojih7o1f.bkt.clouddn.com/";

	public static final String RESPONSE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><response><error>0</error><message></message></response>";

	/**
	 * 通用成功返回
	 */
	public static final String COM_RESP =  "{\"ret\":0}";

	/**
	 * 异常失败返回
	 */
	public static final String COM_FAIL_RESP =  "{\"ret\":-1}";

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
	 * 缓存
	 */
	public static final String TEST_CACHE = "testCache";

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

	/**
	 * 评论或点赞类型 note 帖子
	 */
	public static final int NOTE_TYPE = 0;
	/**
	 * 评论或点赞类型 infor 资讯
	 */
	public static final int INFOR_TYPE = 1;
	/**
	 * 主页评论或点赞型 homepage
	 */
	public static final int PAGE_TYPE = 2;
	/**
	 * 举报 圈子类型
	 */
	public static final int CIRCLE_TYPE = 3;
	/**
	 * 聚会评论  party 类型
	 */
	public static final int PARTY_TYPE = 4;
	/**
	 * 动态 类型
	 */
	public static final int DYNAMIC_TYPE = 5;

	/**
	 * 聊天 类型
	 */
	public static final int CHAT_TYPE = 6;

	/**
	 * 文件 类型
	 */
	public static final int FILE_TYPE = 7;
	/**
	 * 帖子评论点赞
	 */
	public static final int NOTE_COM_TYPE = 8;
	/**
	 * 资讯评论点赞
	 */
	public static final int INFOR_COM_TYPE = 9;
	/**
	 * 聚会评论
	 */
	public static final int PARTY_COM_TYPE = 10;



	/**
	 * 举报
	 */
	public static final int FEED_TIPS = 1;
	/**
	 * 反馈
	 */
	public static final int FEED_BACK= 0;
	/**
	 *
	 */
	public static final int LEVEL_HOUR= 0;
	/**
	 *
	 */
	public static final int LEVEL_PARTY= 1;
	/**
	 *
	 */
	public static final int LEVEL_NOTE= 2;
	/**
	 *
	 */
	public static final int LEVEL_COMMENT= 3;
	/**
	 *
	 */
	public static final int LEVEL_TRANS= 4;
	/**
	 *
	 */
	public static final int LEVEL_SHARE= 5;
	/**
	 *
	 */
	public static final int LEVEL_CIRCLE= 6;


	/**
	 * 单人聊天
	 */
	public static final int ROOM_SINGLE = 1;
	/**
	 * 群聊
	 */
	public static final int ROOM_MULIT = 2;
	/**
	 * 面对面聊
	 */
	public static final int ROOM_FACE_2_FACE= 3;

	/**
	 * 置顶
	 */
	public static final int NOTE_TOP = 0;
	/**
	 * 加精
	 */
	public static final int NOTE_JIAJING = 1;



	public static final int CIRCLE_VISIT= 0;
	/**
	 *
	 */
	public static final int CIRCLE_PARTY= 1;
	/**
	 *
	 */
	public static final int CIRCLE_NOTE= 2;
	/**
	 *
	 */
	public static final int CIRCLE_COMMENT= 3;
	/**
	 *
	 */
	public static final int CIRCLE_TRANS= 4;
	/**
	 *
	 */
	public static final int CIRCLE_SHARE= 5;
	/**
	 *
	 */
	public static final int CIRCLE_THUMP= 6;
	/**
	 *
	 */
	public static final int CIRCLE_PARTY_VISIT= 7;
	/**
	 *
	 */
	public static final int CIRCLE_PARTY_SHARE = 8;
	/**
	 *
	 */
	public static final int CIRCLE_PARTY_THUMP= 9;

	public static final int CIRCLE_NOTE_VISIT =10;


	public static final int ONE = 1;

	public static final int TWO = 2;

	public static final int THREE = 3;

	public static final int FOUR = 4;

	/**
	 * 收藏 图片类型
	 */
	public static final int COLLET_PIC = 1;
	/**
	 * 收藏 音乐类型
	 */
	public static final int COLLET_MUSIC = 2;
	/**
	 * 收藏 视频类型
	 */
	public static final int COLLET_VIDEO = 3;
	/**
	 * 收藏 语音类型
	 */
	public static final int COLLET_VOICE = 4;
	/**
	 * 收藏 链接文章、圈子 、帖子、聚会类型
	 */
	public static final int COLLET_URL = 5;
	/**
	 * 健康
	 */
	public static final int INFOR_HEALTH = 1;
	/**
	 * 安防
	 */
	public static final int INFOR_SECRITY = 2;
	/**
	 * 广播
	 */
	public static final int INFOR_RADIO = 3;
	/**
	 * 视频
	 */
	public static final int INFOR_VIDEO = 4;



	public static final String QUICK_LOGIN =
			"您已成功登录“天天老友” 。登录账号为您本次登录使用的手机号码，登录初始密码为您本次登录使用的手机号码后6位。为了您的账户安全，“天天老友”建议您及时修改登录密码";

}
