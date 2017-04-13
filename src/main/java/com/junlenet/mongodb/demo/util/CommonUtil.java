package com.junlenet.mongodb.demo.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

public class CommonUtil {
	public static boolean isRightPhone(String phone) {
		String regExp = "^1(3|4|5|7|8)\\d{9}$";
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(phone);
		return m.find();
	}

	public static String getSHA256(String content) {
		MessageDigest digest;
		String output = "";
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(content.getBytes("UTF-8"));
			output = Hex.encodeHexString(hash);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return output;
	}

}
