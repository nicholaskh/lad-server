package com.lad.util;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JPushUtil {

	private static Logger logger = LogManager.getLogger(JPushUtil.class);

	private static String MASTER_SECRET = "db587bc126abddf27e548ecc";
	private static String APP_KEY = "d53e8d39d6df18e379bf5da4";
	public static String APPLY = "申请加我为好友";
	public static String AGREE_APPLY_FRIEND = "同意添加我为好友";
	public static String REFUSE_APPLY_FRIEND = "拒绝添加我为好友";
	public static String MULTI_INSERT = "邀请我加入群聊";

	public static String MEDIA_APPLY_SINGLE = "申请和我语音聊天";

	public static String MEDIA_APPLY_MULTI = "邀请我加入语音聊天";

	public static void pushAll(String content) {
		JPushClient jpushClient = new JPushClient(MASTER_SECRET, APP_KEY, null,
				ClientConfig.getInstance());
		PushPayload payload = buildPushObject_all_alias_alert(content);
		try {
			PushResult result = jpushClient.sendPush(payload);
			logger.info("Got result - " + result);

		} catch (APIConnectionException e) {
			logger.error("Connection error, should retry later : "
					+ e.getMessage());

		} catch (APIRequestException e) {
			logger.error("Should review the error, and fix the request : "
							+ e.getMessage());
			logger.error("HTTP Status: " + e.getStatus());
			logger.error("Error Code: " + e.getErrorCode());
			logger.error("Error Message: " + e.getErrorMessage());
		}
	}

	public static PushPayload buildPushObject_all_alias_alert(String content) {
		return PushPayload.newBuilder().setPlatform(Platform.all())
				.setNotification(Notification.alert(content)).build();
	}

	public static PushResult pushTo(String content, String... alias) {
		JPushClient jpushClient = new JPushClient(MASTER_SECRET, APP_KEY, null,
				ClientConfig.getInstance());
		PushPayload payload = buildPushObject_to_alias_alert(content, alias);
		logger.info("alias: {}",alias);
		try {
			PushResult result = jpushClient.sendPush(payload);
			logger.info("Got result - {}",result);
			return result;
		} catch (APIConnectionException e) {
			logger.error("Connection error, should retry later : {}", e.getMessage());

		} catch (APIRequestException e) {
			logger.error("Should review the error, and fix the request : {}", e.getMessage());
			logger.error("HTTP Status: {}", e.getStatus());
			logger.error("Error Code: {}" ,e.getErrorCode());
			logger.error("Error Message: {}", e.getErrorMessage());
		}
		return null;
	}

	public static PushPayload buildPushObject_to_alias_alert(String content,
			String... alias) {
		return PushPayload.newBuilder().setPlatform(Platform.all())
				.setAudience(Audience.alias(alias))
				.setNotification(Notification.alert(content)).build();
	}

	public static PushPayload buildPushObject_android_and_ios(String notification_title, String msg_title, String
			msg_content, String extrasparam) {
		return PushPayload.newBuilder().setPlatform(Platform.android_ios()).setAudience(Audience.all())
				.setNotification(Notification.newBuilder().setAlert(notification_title).addPlatformNotification(AndroidNotification.newBuilder()
						.setAlert(notification_title).setTitle(notification_title)

				//此字段为透传字段，不会显示在通知栏。用户可以通过此字段来做一些定制需求，如特定的key传要指定跳转的页面（value）
				.addExtra("androidNotification extras key", extrasparam).build()).addPlatformNotification(IosNotification.newBuilder()
				//传一个IosAlert对象，指定apns title、title、subtitle等
				.setAlert(notification_title)
				//直接传alert
				//此项是指定此推送的badge自动加1
				.incrBadge(1)
				//此字段的值default表示系统默认声音；传sound.caf表示此推送以项目里面打包的sound.caf声音来提醒，
				// 如果系统没有此音频则以系统默认声音提醒；此字段如果传空字符串，iOS9及以上的系统是无声音提醒，以下的系统是默认声音
				.setSound("sound.caf")
				//此字段为透传字段，不会显示在通知栏。用户可以通过此字段来做一些定制需求，如特定的key传要指定跳转的页面（value）
				.addExtra("iosNotification extras key", extrasparam)
				//此项说明此推送是一个background推送，想了解background看：http://docs.jpush.io/client/ios_tutorials/#ios-7-background-remote-notification
				// .setContentAvailable(true)

				.build()).build())
				//Platform指定了哪些平台就会像指定平台中符合推送条件的设备进行推送。 jpush的自定义消息，
				// sdk默认不做任何处理，不会有通知提示。建议看文档http://docs.jpush.io/guideline/faq/的
				// [通知与自定义消息有什么区别？]了解通知和自定义消息的区别
				.setMessage(Message.newBuilder().setMsgContent(msg_content).setTitle(msg_title).addExtra("message extras key", extrasparam).build())

				.setOptions(Options.newBuilder()
						//此字段的值是用来指定本推送要推送的apns环境，false表示开发，true表示生产；对android和自定义消息无意义
						.setApnsProduction(false)
						//此字段是给开发者自己给推送编号，方便推送者分辨推送记录
						.setSendno(1)
						//此字段的值是用来指定本推送的离线保存时长，如果不传此字段则默认保存一天，最多指定保留十天，单位为秒
						.setTimeToLive(86400).build()).build();
	}

	public static void main(String[] args) {
		JPushUtil.pushTo("Test from B","591e984231f0a5786e240f6c");
	}
}
