package com.lad.util;

import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.lad.bo.OptionBo;
import com.lad.bo.RequireBo;
import com.lad.bo.WaiterBo;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {

	private static Logger logger = LogManager.getLogger(CommonUtil.class);
	/**
	 * 每天多少毫秒时间
	 */
	private static long dayMinlls = 1000L * 60 * 60 * 24 * 180;

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
			logger.error("密码加密错误：{}",e);
		} catch (UnsupportedEncodingException e) {
			logger.error("密码加密错误：{}",e);
		}
		return output;
	}

	/**
	 * 上传video是，获取缩略图
	 * @param file
	 * @param path
	 * @param filename
	 * @param due
	 * @return 0 是video 的路径， 1 是缩略图路径
	 */
	public static String[] uploadVedio(MultipartFile file, String path, String filename, int due) {
		File targetFile = new File(path, filename);
		String vedio = "";
		String pic = "";
		try {
			if (!targetFile.exists()) {
				targetFile.mkdirs();
				file.transferTo(targetFile);
			}
			String outName = FFmpegUtil.transfer(targetFile, path);
			if (StringUtils.isEmpty(outName)) {
				outName = FFmpegUtil.transfer(targetFile, path);
			}
			if (StringUtils.isNotEmpty(outName)) {
				if (due == 0) {
					pic = QiNiu.uploadToQiNiu(path, outName);
				} else {
					pic = QiNiu.uploadToQiNiuDue(path, outName, due);
				}
				File picfile = new File(path, outName);
				picfile.delete();
			}
			if (due == 0) {
				vedio = QiNiu.uploadToQiNiu(path, filename);
			} else {
				vedio = QiNiu.uploadToQiNiuDue(path, filename, due);
			}
			targetFile.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String[] res = new String[]{
				Constant.QINIU_URL + vedio + "?v=" + CommonUtil.getRandom1(),
				Constant.QINIU_URL + pic + "?v=" + CommonUtil.getRandom1()
		};
		return res;
	}

	/**
	 * 上传图片
	 * @param file
	 * @param path
	 * @param filename
	 * @param due
	 * @return
	 */
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
			message = URLEncoder.encode(message, "GBK");
		} catch (UnsupportedEncodingException ex) {
		}
		String url = "http://sms-gw.bjedu.cloud:9888/smsservice/SendSMS?UserId=100535&Password=ttlyyl_2017&Mobiles="+mobile+"&Content="+message+"&ExtNo=35";
		String responseString = HttpClientUtil.getInstance().doGetRequest(url);
		logger.error("{} : =====message send result : {}",mobile,responseString);
		if (responseString.trim().equals(Constant.RESPONSE)) {
			return 0;
		}
		return -1;
	}

	/**
	 * 获取短息发送list
	 * @return
	 */
	public static String getSMSReport() {
		String url = "http://sms-gw.bjedu.cloud:9888/smsservice/ReceiveReport?UserId=100535&Password=ttlyyl_2017&ExtNo=35";
		return HttpClientUtil.getInstance().doGetRequest(url);
	}

	/**
	 * 获取短息发送list
	 * @return
	 */
	public static String getSMSReport2() {
		String url = "http://sms-gw.bjedu.cloud:9888/smsservice/ReceiveReport?UserId=100535&Password=ttlyyl_2017";
		return HttpClientUtil.getInstance().doGetRequest(url);
	}

	public static int getRandom1() {
		return (int) (1 + Math.random() * (10));
	}

	public static String getRandom() {
		return String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
	}


	public static String buildCodeMsg(String number) {
		String msg = "";
		try {
			String new1 =  new String("您的验证码为：".getBytes(), "UTF-8");
			String new2 =  new String("，该验证码5分钟内有效，如非本人操作，请忽略。".getBytes(), "UTF-8");
			StringBuilder builder = new StringBuilder();
			builder.append(new1).append(number).append(new2);
			msg = builder.toString();
		} catch (Exception e) {
			logger.error("send Msg exception : {}",e.getMessage());
		}
		return msg;
	}

	public static String buildPassMsg(String number) {
		String msg = "";
		try {
			StringBuilder builder = new StringBuilder();
			builder.append("您正在修改密码，验证码为：").append(number).append("，该验证码5分钟内有效，如非本人操作，请忽略。");
			msg = new String(builder.toString().getBytes(), "UTF-8");
		} catch (Exception e) {
			logger.error("send Msg exception : {}",e.getMessage());
		}
		return msg;
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
	 * @param time 目标时间
	 * @return ture
	 */
	public static boolean isTimeIn(long time){
		return (System.currentTimeMillis() - time) <= 300000;
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

	/**
	 * 根据逗号将数据列表打散
	 * @param ids
	 * @return
	 */
	public static String[] getIds(String ids){
		String[] idsArr;
		if (ids.indexOf(',') > -1) {
			idsArr = ids.split(",");
		} else {
			idsArr = new String[]{ids};
		}
		return idsArr;
	}


	/**
	 * 分页查询
	 * @param query
	 * @param startId  开始主键
	 * @param gt
	 * @param limit
	 */
	public static void queryByIdPage(Query query, String startId, boolean gt, int limit){
		query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
		if (!StringUtils.isEmpty(startId)) {
			if (gt) {
				query.addCriteria(new Criteria("_id").gt(startId));
			} else {
				query.addCriteria(new Criteria("_id").lt(startId));
			}
		}
		if (limit < 0 || limit > 500) {
			limit = 10;
		}
		query.limit(limit);
	}


	/**
	 * 判断时间是否在180天之内
	 * @param currenDate 当前时间
	 * @return 180天前的日期
	 */
	public static Date getHalfYearTime(Date currenDate){
		long stap = currenDate.getTime() - dayMinlls ;
		return new Date(stap);
	}

	/**
	 * 获取当前时间字符串
	 * @param currenDate 当前时间
	 * @return yyyy-MM-dd
	 */
	public static String getCurrentDate(Date currenDate){
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		return sf.format(currenDate);
	}

	/**
	 * 获取当前时间字符串
	 * @param dateStr yyyy-MM-dd HH:mm:ss
	 * @return date
	 */
	public static Date getDate(String dateStr) throws ParseException{
		if ("0".equals(dateStr) || StringUtils.isEmpty(dateStr)) {
			return null;
		}
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sf.parse(dateStr);
	}

	/**
	 * 获取当前时间字符串
	 * @param dateStr yyyy-MM-dd HH:mm:ss
	 * @return date
	 */
	public static Date getDate(String dateStr, String format){
		SimpleDateFormat sf = new SimpleDateFormat(format);
		try {
			return sf.parse(dateStr);
		} catch (ParseException e) {
			 logger.error(e);
		}
		return null;
	}

	/**
	 * 获取当前时间字符串
	 * @param date 当前时间
	 * @return yyyy-MM-dd
	 */
	public static String getDateStr(Date date, String format){
		SimpleDateFormat sf = new SimpleDateFormat(format);
		return sf.format(date);
	}
	/**
	 * 获取当前时间零点时间戳
	 * @return
	 */
	public static Date getZeroDate(Date currenDate){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currenDate);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 获取当前天23：59:59时间戳
	 * @return
	 */
	public static Date getLastDate(Date currenDate){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currenDate);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime();
	}


	/**
	 * 判断当前对象是否为空
	 * @param collection 当前对象
	 * @return
	 */
	public static boolean isEmpty(Collection collection){
		return collection == null || collection.isEmpty();
	}

	/**
	 * 目前每次启动时加载
	 */
	private static HashSet<String> adminUserids;
	static {
		adminUserids = new LinkedHashSet<>();
		// to add
		adminUserids.add("");
	}
	/**
	 * 获取管理员id信息
	 * @return
	 */
	public static HashSet<String> getAdminUserids() {
		return adminUserids;
	}

	public static String ff(String inFile, String outFile){


		String command = "ffmpeg -i " + inFile + " -y -f image2 -ss 00:00:10 -t 00:00:01 -s 176x144 " + outFile;


		return "";

	}

	/**
	 * 根据生日计算年龄
	 * @param birth
	 * @return
	 */
	public static int getAge(Date birth){
		Calendar birthTime = Calendar.getInstance();
		birthTime.setTime(birth);
		int birthYear = birthTime.get(Calendar.YEAR);
		int birthMonth = birthTime.get(Calendar.MONTH)+1;
		int birthDay = birthTime.get(Calendar.DAY_OF_MONTH);
		
		Calendar now = Calendar.getInstance();
		int nowYear = now.get(Calendar.YEAR);
		int nowMonth = now.get(Calendar.MONTH)+1;
		int nowDay = now.get(Calendar.DAY_OF_MONTH);
		
		int age = 0;
//		System.out.println(nowYear+"     "+birthYear);
		if(nowMonth-birthMonth>=0 && nowDay-birthDay>=0){
			age = nowYear-birthYear;
		}else{
			age = nowYear-birthYear-1;
		}
		return age;
	}
	
	/**
	 * 计算匹配度
	 * @param mongoTemplate
	 * @param requireBo
	 * @param waiterBo
	 * @return
	 */
	public static Map getMatch(MongoTemplate mongoTemplate,RequireBo requireBo, WaiterBo waiterBo) {
		int matchNum = 0;
		
		// 基础条件匹配
		int baseNum = 0;
		if(requireBo.getNowin()!=null && requireBo.getNowin()!="" && requireBo.getNowin().equals(waiterBo.getNowin())){
			baseNum+=20;
		}
		if(requireBo.getMarriaged()!=null  && requireBo.getMarriaged().equals(waiterBo.getMarriaged())){
			baseNum+=20;
		}
		
		// 其他条件匹配
		int otherNum=0;
		
		// 工作匹配
		if(requireBo.getJob()!=null && requireBo.getJob().contains(waiterBo.getJob()) ){
			otherNum+=10;
		}
		// 兴趣匹配
		int hobbyMacthNum = 0;
		if(waiterBo.getHobbys()!=null){
			for (String hobbys : waiterBo.getHobbys()) {
				if(requireBo.getHobbys().contains(hobbys)){
					hobbyMacthNum++;
				}
			}
			if(hobbyMacthNum>=1){
				int size = requireBo.getHobbys().size();
				int round = Math.round(hobbyMacthNum/size*40);
				otherNum+=Math.round((round+60)*0.1);
			}
		}

		
		// 学历匹配
		int educationMatch = 0;
		// 如果学历不限,或者基础资料学历大于要求学历
		Integer requireEducation = requireBo.getEducation();
		Integer waiterEducation = waiterBo.getEducation();
		if(requireEducation !=null && waiterEducation!=null){
			if(requireEducation== 0 || waiterEducation-requireEducation>=0){
				educationMatch = 100;
			}
		}
		
		otherNum+=educationMatch*0.1;
		
		// 收入匹配
		int salaryNum = 0;
		
		Query salarQuery = new Query(Criteria.where("value").is(waiterBo.getSalary()));
		OptionBo waiterOptoins = mongoTemplate.findOne(salarQuery, OptionBo.class);
		salarQuery = new Query(Criteria.where("value").is(requireBo.getSalary()));
		OptionBo requireOptoins = mongoTemplate.findOne(salarQuery, OptionBo.class);
	
		if(waiterOptoins != null && requireOptoins !=null){
			Integer waiterSort = waiterOptoins.getSort();
			Integer requireSort = requireOptoins.getSort();
			if(requireSort == 0 || waiterSort - requireSort>=0){
				salaryNum = 100;
			}
		}

		otherNum+=salaryNum*0.3;
		
		// 身高匹配
		int hightMatch = 0;
		Integer waiterHight = waiterBo.getHight();
		if(waiterHight!=null){
			String[] split = requireBo.getHight().replace("厘米", "").split("-");
			int minHight = Integer.valueOf(split[0]);
			int maxHight = Integer.valueOf(split[1]);
			
			if(waiterHight>=minHight && waiterHight<=maxHight){
				hightMatch =100;
			}
			if(waiterHight<minHight){
				hightMatch = (100 - 50*(minHight-waiterHight)/10>0)?(100 - 50*(minHight-waiterHight)/10):0;
			}
			if(waiterHight>maxHight){
				hightMatch = (100 - 30*(waiterHight-maxHight)/10>0)?(100 - 50*(minHight-waiterHight)/10):0;
			}
		}

		otherNum += hightMatch*0.3;
		
		// 年龄匹配
		int ageMatch = 0;
		Integer waiterAge = waiterBo.getAge();
		if(waiterAge!=null){
			String[] split2 = requireBo.getAge().replace("岁", "").split("-");
			int minAge = Integer.valueOf(split2[0]);
			int maxAge = Integer.valueOf(split2[1]);
			
			if(waiterAge>=minAge && waiterAge<=maxAge){
				ageMatch =100;
			}
			if(waiterAge<minAge){
				ageMatch = (100 - 30*(minAge-waiterAge)/10>0)?(100 - 30*(minAge-waiterAge)/10):0;
			}
			if(waiterAge>maxAge){
				ageMatch = (100 - 50*(waiterAge-maxAge)/10>0)?(100 - 50*(waiterAge-maxAge)/10):0;
			}
		}

		otherNum += ageMatch*0.3;
		
		matchNum = (int) (baseNum +Math.round(otherNum*0.6));
		
		Map map = new HashMap<>();
		map.put("match", matchNum);
		map.put("waiter", waiterBo);
		return map;
	}
	

	
	/**
	 * fastJson字段过滤器
	 * @param obj 需要过滤字段的实体
	 * @param need 过滤还是保留
	 * @param params 需要过滤(保留)的字段名
	 * @return
	 */
	public static String fastJsonfieldFilter(Object obj,boolean need,String... params) {
		PropertyFilter profilter = new PropertyFilter(){  	  
            @Override  
            public boolean apply(Object object, String name, Object value) {
            	for (String string : params) {
            		if(name.equalsIgnoreCase(string)){  
                        return need;  
                    }
				}
                  System.out.println(!need);
                return !need;  
            }  
        }; 
        String json = JSON.toJSONString(obj, profilter);
		return json;
	}
	
	/**
	 * fastJson字段过滤器
	 * @param obj 需要过滤字段的实体
	 * @param need 过滤还是保留
	 * @param params 需要过滤(保留)的字段名
	 * @return
	 */
	public static Object fieldFilter(Object obj,boolean need,String... params) {
		PropertyFilter profilter = new PropertyFilter(){  
			  
            @Override  
            public boolean apply(Object object, String name, Object value) {
            	for (String string : params) {
            		if(name.equalsIgnoreCase(string)){  
                        return need;  
                    }
				}
                  
                return !need;  
            }  
        }; 
        Object json = JSON.toJSON(obj);
		return json;
	}
}
