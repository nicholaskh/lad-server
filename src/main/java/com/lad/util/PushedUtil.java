package com.lad.util;

import com.pushd.ImAssistant;
import com.pushd.Message;

public class PushedUtil {

	public static ImAssistant getPushed(String ip, int port) {
		ImAssistant assistent = ImAssistant.init(ip, port);
		if (assistent == null) {
			System.out.println("初始化失败");
			return null;
		}
		if (!assistent.setReadTime(7000)) {
			assistent.close();
			return null;
		}
		return assistent;
	}

	public static Message getTokenFromPushed(ImAssistant imAssistant) {
		if (null == imAssistant) {
			return null;
		}
		Message token = imAssistant.getToken();
		if (null != token) {
			return token;
		}
		return null;
	}

	public static boolean subscribeToPushed(ImAssistant imAssistant, String channelName, String channelId,
			String... uuids) {
		if (null == imAssistant) {
			return false;
		}
		imAssistant.authServer("cdb5d308e9cd308822f95b8f2c61dbf4289f127b8c46193941aa7fbc9f79d905");
		Message message = imAssistant.subscribe(channelName, channelId, uuids);
		if (message.getStatus() == Message.Status.success) {
			imAssistant.setServerToken(message.getMsg());
			return true;
		}
		return false;
	}
	
	public static Message getToken() {
		ImAssistant imAssistant = PushedUtil.getPushed("180.76.173.200", 2222);
		if (null == imAssistant) {
			return null;
		}
		Message token = PushedUtil.getTokenFromPushed(imAssistant);
		if (null == token) {
			return null;
		}
		return token;
	}
}