package com.lad.util;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.PushPayload;

public class JPushUtil {

	private static String MASTER_SECRET = "db587bc126abddf27e548ecc";
	private static String APP_KEY = "d53e8d39d6df18e379bf5da4";

	public static void push() {
		// JPushClient jpush = new JPushClient(masterSecret, appKey);
		JPushClient jpushClient = new JPushClient(MASTER_SECRET, APP_KEY, null,
				ClientConfig.getInstance());
		 PushPayload payload = buildPushObject_all_alias_alert();
		try {
	        PushResult result = jpushClient.sendPush(payload);
	        System.out.println("Got result - " + result);

	    } catch (APIConnectionException e) {
	        // Connection error, should retry later
	    	System.out.println("Connection error, should retry later : "+ e.getMessage());

	    } catch (APIRequestException e) {
	        // Should review the error, and fix the request
	    	System.out.println("Should review the error, and fix the request : "+e.getMessage());
	    	System.out.println("HTTP Status: " + e.getStatus());
	    	System.out.println("Error Code: " + e.getErrorCode());
	    	System.out.println("Error Message: " + e.getErrorMessage());
	    }
	}
	
	public static PushPayload buildPushObject_all_alias_alert() {
		 return PushPayload.alertAll("ALERT test");
    }
	
	public static void main(String [] args){
		JPushUtil.push();
	}
}
