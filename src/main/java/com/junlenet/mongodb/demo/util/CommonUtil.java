package com.junlenet.mongodb.demo.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {
	public static boolean isRightPhone(String phone){
		String regExp = "^1(3|4|5|7|8)\\d{9}$";
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(phone);
		return m.find();
	}
}
