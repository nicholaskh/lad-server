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
import cn.jpush.api.push.model.notification.Notification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;

import java.util.Map;

public class JPushUtil {

	private static Logger logger = LogManager.getLogger(JPushUtil.class);

//	private static String MASTER_SECRET = "db587bc126abddf27e548ecc";
//	private static String APP_KEY = "d53e8d39d6df18e379bf5da4";
	private static String MASTER_SECRET = "5c0c7744590b80fae9f069e2";
	private static String APP_KEY = "ca8cc37939809b64829b5eeb";

	public static String APPLY = "请求添加您为好友";
	public static String AGREE_APPLY_FRIEND = "同意添加我为好友";
	public static String REFUSE_APPLY_FRIEND = "拒绝添加我为好友";
	public static String MULTI_INSERT = "邀请我加入群聊";

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

	@Async
	public static PushResult pushTo(String content, String... alias) {
		JPushClient jpushClient = new JPushClient(MASTER_SECRET, APP_KEY, null,
				ClientConfig.getInstance());
		PushPayload payload = buildPushObject_to_alias_alert(content, alias);
		logger.info("push alias {},  pushInfo  : {}", alias,   content);
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


	@Async
	public static void push(String title, String content, String path,
								  String... alias) {
		try {
			JPushClient jpushClient = new JPushClient(MASTER_SECRET, APP_KEY, null,
					ClientConfig.getInstance());
			logger.info("push alias {},  push title : {},  pushInfo  : {}", alias,  title,  content);
			PushPayload payload = buildPushObject_to_alias_alert(title, content, path, alias);
			PushResult result = jpushClient.sendPush(payload);
			logger.info("Got result - {}",result);
			if (result.getResponseCode() != 200) {
				logger.error("push message fail  {}", result.getResponseCode());
			}
		} catch (APIConnectionException e) {
			logger.error("Connection error, should retry later : {}", e.getMessage());
		} catch (APIRequestException e) {
			logger.error("Should review the error, and fix the request : {}", e.getMessage());
			logger.error("HTTP Status: {}", e.getStatus());
			logger.error("Error Code: {}" ,e.getErrorCode());
			logger.error("Error Message: {}", e.getErrorMessage());
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Async
	public static void pushNotify(String title, String content, String path,
							String... alias) {
		try {
			JPushClient jpushClient = new JPushClient(MASTER_SECRET, APP_KEY, null,
					ClientConfig.getInstance());
			logger.info("push alias {},  push title : {},  pushInfo  : {}", alias,  title,  content);
			PushPayload payload = buildPushObject_to_alias_notify(title, content, path, alias);
			PushResult result = jpushClient.sendPush(payload);
			logger.info("Got result - {}",result);
			if (result.getResponseCode() != 200) {
				logger.error("push message fail  {}", result.getResponseCode());
			}
		} catch (APIConnectionException e) {
			logger.error("Connection error, should retry later : {}", e.getMessage());
		} catch (APIRequestException e) {
			logger.error("Should review the error, and fix the request : {}", e.getMessage());
			logger.error("HTTP Status: {}", e.getStatus());
			logger.error("Error Code: {}" ,e.getErrorCode());
			logger.error("Error Message: {}", e.getErrorMessage());
		} catch (Exception e) {
			logger.error(e);
		}
	}


	public static PushPayload buildPushObject_to_alias_notify(String title, String content, String path,
															 String... alias) {
		return PushPayload.newBuilder().setPlatform(Platform.all())
				.setAudience(Audience.alias(alias))
				.setNotification(Notification.newBuilder().
						setAlert(content)
						.addPlatformNotification(AndroidNotification.newBuilder()
								.setAlert(content)
								.setTitle(title)
								.addExtra("path", path).build())
						.build())
				.setOptions(Options.newBuilder()
						.setTimeToLive(432000).build())
				.build();
	}


	/**
	 * 推送通知
	 * @param title  推送标题
	 * @param content  推送内容
	 * @param path  推送落地页路径
	 * @param alias  推送人
	 * @return
	 */
	public static PushPayload buildPushObject_to_alias_alert(String title, String content, String path,
															 String... alias) {
		return PushPayload.newBuilder().setPlatform(Platform.all())
				.setAudience(Audience.alias(alias))
				.setNotification(Notification.newBuilder().
						setAlert(content)
						.addPlatformNotification(AndroidNotification.newBuilder()
								.setAlert(content)
								.setTitle(title)
								.addExtra("path", path).build())
						.build())
				.setMessage(Message.newBuilder()
						.setMsgContent(content)
						.setTitle(title)
						.addExtra("path", path).build())
				.setOptions(Options.newBuilder()
						.setTimeToLive(432000).build())
				.build();
	}


	@Async
	public static boolean pushMessage(String title, String content, String path,
							   String... alias) {
		JPushClient jpushClient = new JPushClient(MASTER_SECRET, APP_KEY, null,
				ClientConfig.getInstance());
		PushPayload payload = buildPushObject_to_Message(title, content, path, alias);
		logger.info("pushMessage alias {},  push title : {},  pushInfo  : {}", alias,  title,  content);
		try {
			PushResult result = jpushClient.sendPush(payload);
			logger.info("Got result - {}",result);
			return result.getResponseCode() == 200;
		} catch (APIConnectionException e) {
			logger.error("Connection error, should retry later : {}", e.getMessage());
		} catch (APIRequestException e) {
			logger.error("Should review the error, and fix the request : {}", e.getMessage());
			logger.error("HTTP Status: {}", e.getStatus());
			logger.error("Error Code: {}" ,e.getErrorCode());
			logger.error("Error Message: {}", e.getErrorMessage());
		}
		return false;
	}

	@Async
	public static boolean pushParams(String title, String content, String path,
									 Map<String, String> params, String... alias) {
		JPushClient jpushClient = new JPushClient(MASTER_SECRET, APP_KEY, null,
				ClientConfig.getInstance());
		try {
			PushPayload payload = buildPushObject_to_alias_params(title, content, path, params, alias);
			logger.info("pushParams alias {},  push params : {},  pushInfo  : {}", alias,  params,  content);
			PushResult result = jpushClient.sendPush(payload);
			logger.info("Got result - {}",result);
			return result.getResponseCode() == 200;
		} catch (APIConnectionException e) {
			logger.error("Connection error, should retry later : {}", e.getMessage());
		} catch (APIRequestException e) {
			logger.error("Should review the error, and fix the request : {}", e.getMessage());
			logger.error("HTTP Status: {}", e.getStatus());
			logger.error("Error Code: {}" ,e.getErrorCode());
			logger.error("Error Message: {}", e.getErrorMessage());
		}
		return false;
	}

	/**
	 * 推送消息
	 * @param title  推送标题
	 * @param content  推送内容
	 * @param path  推送落地页路径
	 * @param alias  推送人
	 * @return
	 */
	public static PushPayload buildPushObject_to_Message(String title, String content, String path,
															 String... alias) {
		return PushPayload.newBuilder().setPlatform(Platform.all())
				.setAudience(Audience.alias(alias))
				.setMessage(Message.newBuilder()
						.setMsgContent(content)
						.setTitle(title)
						.addExtra("path", path).build())
				.setOptions(Options.newBuilder()
						.setTimeToLive(432000).build())
				.build();
	}


	/**
	 * 推送通知,附带参数信息
	 * @param title  推送标题
	 * @param content  推送内容
	 * @param path  推送落地页路径
	 * @param params 参数信息
	 * @param alias  推送人
	 * @return
	 */
	public static PushPayload buildPushObject_to_alias_params(String title, String content, String path,  Map<String, String> params,
															  String... alias) {
		return PushPayload.newBuilder().setPlatform(Platform.all())
				.setAudience(Audience.alias(alias))
				.setNotification(Notification.newBuilder().
						setAlert(content)
						.addPlatformNotification(AndroidNotification.newBuilder()
								.setAlert(content)
								.setTitle(title)
								.addExtra("path", path).addExtras(params).build())
						.build())
				.setMessage(Message.newBuilder()
						.setMsgContent(content)
						.setTitle(title)
						.addExtra("path", path).addExtras(params).build())
				.setOptions(Options.newBuilder()
						.setTimeToLive(432000).build())
				.build();
	}

	public static void main(String[] args) {
		JPushUtil.pushTo("Test from B","591e984231f0a5786e240f6c");
	}
}
