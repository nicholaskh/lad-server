package com.lad.util;

import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Hex;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	public static String upload(MultipartFile file, String path, String filename, int due) {
		File targetFile = new File(path, filename);
		String result = "";
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}
		try {
			file.transferTo(targetFile);
			if (due == 0) {
				result = QiNiu.uploadToQiNiu(path, filename);
			} else {
				result = QiNiu.uploadToQiNiuDue(path, filename, due);
			}
			targetFile.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Constant.QINIU_URL + result + "?v=" + CommonUtil.getRandom1();
	}

	public static String toErrorResult(int ret, String error) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("ret", ret);
		map.put("error", error);
		return JSONObject.fromObject(map).toString();
	}

	public static int sendSMS1(String mobile, String message) {
		try {
			message = URLEncoder.encode(message, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
		}
		String url = "http://hprpt2.eucp.b2m.cn:8080/sdkproxy/sendsms.action?cdkey=0SDK-EBB-6699-RHSLQ&password=797391&phone="
				+ mobile + "&message=" + message;
		String responseString = HttpClientUtil.getInstance().doGetRequest(url);
		if (responseString.trim().equals(Constant.RESPONSE)) {
			return 0;
		}
		return -1;
	}
	
	public static int sendSMS2(String mobile, String message) {
		try {
			message = URLEncoder.encode(message, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
		}
		String url = "http://118.145.22.174/http20/access/SendMt.do?UserId=1005&Password=MhHv6XuPZr&Mobiles="+mobile+"&Content="+message+"&ExtNo=35";
		String responseString = HttpClientUtil.getInstance().doGetRequest(url);
		if (responseString.trim().equals(Constant.RESPONSE)) {
			return 0;
		}
		return -1;
	}

	public static int getRandom1() {
		return (int) (1 + Math.random() * (10));
	}
	
	/**
	 * 将时间转成字符串
	 * @param date 时间
	 * @return  yyyy-MM-dd HH:mm:ss
	 */
	public static String time2str(Date date){
		return date2Str("yyyy-MM-dd HH:mm:ss", date);
	}
    /**
     * 将日期转换成制定格式
     * @param format  日期格式如 yyyy-MM-dd HH:mm:ss
     * @param date  传入时间
     * @return  时间字符串
     */
    public static String date2Str(String format, Date date){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

	/**
	 * 判断可变形参是否为空
	 * @param ids 参数
	 * @return  true if empty
	 */
	public static boolean isEmpty(String... ids){
    	return ids == null || ids.length == 0;
	}


	/**
	 * 目标时间距离当前时间是否在time之内
	 * @param beforeDate 目标时间
	 * @param time 单位毫秒
	 * @return ture
	 */
	public static boolean isTimeOut(Date beforeDate, long time){
		long stap = System.currentTimeMillis() - beforeDate.getTime() ;
		return time >= stap;
	}

	/**
	 * 目标时间距离当前时间是否在10分钟内
	 * @param beforeDate 目标时间
	 * @return ture
	 */
	public static boolean isTimeInTen(Date beforeDate){
		return isTimeOut(beforeDate, 10*60*1000);
	}

	/**
	 * 获取当前时间一周以前时期
	 * @return
	 */
	public static Date getBeforeWeekDate(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, -7);
		return calendar.getTime();
	}

	/**
	 * 获取当前时间每年的第几周
	 * @return week no
	 */
	public static int getWeekOfYear(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setTime(date);
		return calendar.get(Calendar.WEEK_OF_YEAR);
	}

	/**
	 * 获取当前时间的年份
	 * @return year
	 */
	public static int getYear(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR);
	}

	

}
