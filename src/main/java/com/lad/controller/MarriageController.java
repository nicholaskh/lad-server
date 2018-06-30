package com.lad.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanComparator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.lad.bo.FriendsBo;
import com.lad.bo.RequireBo;
import com.lad.bo.UserBo;
import com.lad.bo.WaiterBo;
import com.lad.service.IFriendsService;
import com.lad.service.IMarriageService;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.ERRORCODE;
import com.lad.vo.CareResultVo;
import com.lad.vo.RequireVo;
import com.lad.vo.WaiterVo;
import com.mongodb.WriteResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;

@Api("儿媳/女婿接口")
@RestController
@RequestMapping("marriage")
@SuppressWarnings("all")
public class MarriageController extends BaseContorller {

	@Autowired
	public IMarriageService marriageService;

	@Autowired
	private IFriendsService friendidService;

	@GetMapping("/search")
	public String search(String keyWord, int type, int page, int limit, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
//		 keyWord = "成";
		List<WaiterBo> list = marriageService.findListByKeyword(keyWord, type, page, limit, WaiterBo.class);

		List resultList = new ArrayList<>();
		for (WaiterBo waiterBo : list) {
			WaiterVo waiterVo = new WaiterVo();
			BeanUtils.copyProperties(waiterBo, waiterVo);
			resultList.add(waiterVo);
		}

		Map map = new HashMap<>();
		map.put("ret", 0);
		map.put("result", resultList);
		return JSONObject.fromObject(map).toString();
	}

	@ApiOperation("查找新发布")
	@GetMapping("/newpublish-search")
	public String getNewPublic(int type, int page, int limit, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		List<WaiterBo> list = marriageService.getNewPublish(type, page, limit, userBo.getId());
		List resultList = new ArrayList<>();
		for (WaiterBo waiterBo : list) {
			WaiterVo waiterVo = new WaiterVo();
			BeanUtils.copyProperties(waiterBo, waiterVo);
			resultList.add(waiterVo);
		}

		if (resultList.size() == 0) {
			return CommonUtil.toErrorResult(ERRORCODE.MARRIAGE_NEWPUBLISH_NULL.getIndex(),
					ERRORCODE.MARRIAGE_NEWPUBLISH_NULL.getReason());
		}
		Map map = new HashMap<>();
		map.put("ret", 0);
		map.put("result", resultList);
		return JSON.toJSONString(map);
	}

	/**
	 * 推荐
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation("推荐")
	@GetMapping("/recommend-search")
	public String getRecommend(String waiterId, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}

		WaiterBo findWaiterById = marriageService.findWaiterById(waiterId);
		if (findWaiterById == null) {
			return CommonUtil.toErrorResult(ERRORCODE.MARRIAGE_PUBLISH_NULL.getIndex(),
					ERRORCODE.MARRIAGE_PUBLISH_NULL.getReason());
		}

		List<Map> list = marriageService.getRecommend(waiterId);

		Map map = new HashMap<>();
		map.put("ret", 0);
		if (list.size() == 0) {
			map.put("result", "数据为空");
		} else {
			Comparator<? super Map> c = new BeanComparator("match").reversed();
			list.sort(c);
			map.put("result", list);
		}
		return JSON.toJSONString(map).replace("\\", "").replace("\"{", "{").replace("}\"", "}");
	}

	@ApiOperation("查询发布详情")
	@GetMapping("/publishe-desc-search")
	public String getPublishDescById(String waiterId, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}

		Map<String, Object> map = new HashMap<>();
		WaiterBo waiter = marriageService.findWaiterById(waiterId);
		if (waiter == null) {
			return CommonUtil.toErrorResult(ERRORCODE.MARRIAGE_PUBLISH_NULL.getIndex(),
					ERRORCODE.MARRIAGE_PUBLISH_NULL.getReason());
		}
		WaiterVo waiterVo = new WaiterVo();
		BeanUtils.copyProperties(waiter, waiterVo);
		map.put("waiter", waiterVo);

		RequireBo require = marriageService.findRequireById(waiterId);
		if(require==null){
			return CommonUtil.toErrorResult(ERRORCODE.MARRIAGE_REQUIRE_NULL.getIndex(),
					ERRORCODE.MARRIAGE_REQUIRE_NULL.getReason());
		}

		RequireVo requireVo = new RequireVo();
		BeanUtils.copyProperties(require, requireVo);
		if (require != null) {
			map.put("require", requireVo);
		} else {
			map.put("require", ERRORCODE.MARRIAGE_QUIRE_NULL.getReason());
		}
		map.put("chatroomid", "非好友");

		// 查看两者是否为好友
		if (!(userBo.getId().equals(waiter.getCreateuid()))) {
			// 根据咨询的发布者id 查询是否为当前用户好友
			List<FriendsBo> friendByFriendid = friendidService.getFriendByFriendid(waiter.getCreateuid());
			if (friendByFriendid != null) {
				for (FriendsBo friendsBo : friendByFriendid) {

					if (waiter.getCreateuid().equals(friendsBo.getFriendid())) {
						map.put("chatroomid", friendsBo.getChatroomid());
					}
				}
			}

		} else {
			map.put("chatroomid", "自己的发布");
		}

		map.put("ret", 0);
		return JSON.toJSONString(map);
	}

	@ApiOperation("不再推荐")
	@PostMapping("/pass")
	public String addPass(String waiterId, String passId, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		System.out.println(waiterId);
		if (waiterId == null || waiterId == "") {
			return CommonUtil.toErrorResult(ERRORCODE.MARRIAGE_PUBLISH_NULL.getIndex(),
					ERRORCODE.MARRIAGE_PUBLISH_NULL.getReason());
		}
		
		// 获取当前用户的黑名单列表		
		Set<String> pass = marriageService.getPass(waiterId);
		pass.add(passId);
		
		Map<String, Object> params = new HashMap<>();
		params.put("pass", pass);
		WriteResult result = marriageService.updateByParams(waiterId, params, WaiterBo.class);

		return Constant.COM_RESP;
	}
	
	@ApiOperation("添加关注")
	@PostMapping("/care-insert")
	public String addCare(String waiterId, String careId, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}

		Map<String, Set<String>> map = marriageService.getCareMap(waiterId);
		
		String time = CommonUtil.getDateStr(new Date(), "yyyy-MM-dd");
		// 如果原map中包含今天的关注,则添加到今天的关注
		Set<String> set = new HashSet<>();
		for (String key : map.keySet()) {
			if (time.equals(key)) {
				set = map.get(key);
				set.add(careId);
				marriageService.updateCare(waiterId, map);
				return Constant.COM_RESP;
			}
		}

		set.add(careId);
		map.put(time, set);
		marriageService.updateCare(waiterId, map);
		return Constant.COM_RESP;
	}

	@ApiOperation("移除关注")
	@PostMapping("/care-delete")
	public String deleteCare(String waiterId, String careId, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}

		Map<String, Set<String>> careMap = marriageService.getCareMap(waiterId);

		for (Entry<String, Set<String>> entity : careMap.entrySet()) {
			Set<String> set = entity.getValue();
			if (set.contains(careId)) {
				set.remove(careId);
				if (set.size() == 0) {
					careMap.remove(entity.getKey());
				}
				break;
			}
		}
		WriteResult updateCare = marriageService.updateCare(waiterId, careMap);

		return Constant.COM_RESP;
	}

	@ApiOperation("查询关注列表")
	@GetMapping("/care-search")
	public String getCares(String waiterId, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}

		Map map = new HashMap<>();
		Map<String, Set<String>> careMap = marriageService.getCareMap(waiterId);

		List resultList = new ArrayList();

		for (String key : careMap.keySet()) {
			CareResultVo re = new CareResultVo();
			// 获取当天的关注列表
			Set<String> set = careMap.get(key);
			List waiterContainer = new ArrayList<>();

			for (String wid : set) {
				WaiterBo waiter = marriageService.findWaiterById(wid);
				if (waiter != null) {
					waiterContainer.add(waiter);
				}
			}

			if (waiterContainer.size() > 0) {
				re.setAddTime(key);
				re.setString(waiterContainer);
				resultList.add(re);
			}
		}

		if (resultList.size() > 0) {
			map.put("ret", 0);
			map.put("result", resultList);
		} else {
			map.put("ret", -1);
			map.put("result", "该账号下无关注或关注的发布已取消");
		}
		return JSON.toJSONString(map).replace("\\", "");
	}

	@ApiOperation("取消发布")
	@PostMapping("/publish-delete")
	public String deletePublish(String waiterId, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}

		WriteResult result = marriageService.deletePublish(waiterId);
		return Constant.COM_RESP;
	}

	@ApiOperation("修改基础资料")
	@PostMapping("/waiter-update")
	public String updateWaiter(@RequestParam String wv, String id, HttpServletRequest request,
			HttpServletResponse response) {

		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (wv == null) {
			// 基础资料错误
			return CommonUtil.toErrorResult(ERRORCODE.MARRIAGE_WAITER_NULL.getIndex(),
					ERRORCODE.MARRIAGE_WAITER_NULL.getReason());
		}

		WriteResult result = update(wv, id, WaiterBo.class);

		return Constant.COM_RESP;
	}

	@ApiOperation("修改要求")
	@PostMapping("/require-update")
	public String updateRequire(@RequestParam String rv, String id, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (rv == null) {
			return CommonUtil.toErrorResult(ERRORCODE.MARRIAGE_REQUIRE_NULL.getIndex(),
					ERRORCODE.MARRIAGE_REQUIRE_NULL.getReason());
		}

		WriteResult result = update(rv, id, RequireBo.class);

		return Constant.COM_RESP;
	}

	@ApiOperation("查询发布")
	@GetMapping("/publishes-search")
	public String getPublishById(HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		String userId = userBo.getId();
		List<WaiterBo> list = marriageService.getPublishById(userId);
		List<WaiterVo> result = new ArrayList<>();
		for (WaiterBo waiterBo : list) {
			WaiterVo waiterVo = new WaiterVo();
			BeanUtils.copyProperties(waiterBo, waiterVo);
			waiterVo.setBirthday(null);
			result.add(waiterVo);
		}
		Map map = new HashMap<>();
		map.put("ret", 0);
		map.put("publishes", result);
		return JSON.toJSONString(map);
	}

	@ApiOperation("发布信息")
	@PostMapping("/insert")
	public String insertPublish(@RequestParam String wv, @RequestParam String rv, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);

		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}

		JSONObject fromObject = JSONObject.fromObject(wv);
		String agree = (String) fromObject.get("agree");
		if (!"true".equals(agree)) {
			return CommonUtil.toErrorResult(ERRORCODE.USER_AGREEMENT_FALSE.getIndex(),
					ERRORCODE.USER_AGREEMENT_FALSE.getReason());
		}

		WaiterBo waiterBo = (WaiterBo) JSONObject.toBean(fromObject, WaiterBo.class);

		String[] brithStrArr = fromObject.get("birthday").toString().toString().split("-");
		Calendar cal = Calendar.getInstance();
		cal.set(Integer.valueOf(brithStrArr[0]), Integer.valueOf(brithStrArr[1]), Integer.valueOf(brithStrArr[2]), 0, 0, 0);

		waiterBo.setBirthday(cal.getTime());
		waiterBo.setAge(CommonUtil.getAge(cal.getTime()));
		waiterBo.setCreateuid(userBo.getId());
		
		
		fromObject = JSONObject.fromObject(rv);
		RequireBo requireBo = (RequireBo) JSONObject.toBean(fromObject, RequireBo.class);
		requireBo.setSex(1 - waiterBo.getSex());
		requireBo.setCreateTime(null);

		// 插入需求,并返回需求id
		String waiterId = marriageService.insertPublish(waiterBo);
		requireBo.setWaiterId(waiterId);
		marriageService.insertPublish(requireBo);

		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("showid", waiterBo.getId());
		return JSONObject.fromObject(map).toString();
	}

	private WriteResult update(String obj, String id, Class clazz) {
		JSONObject fromObject = JSONObject.fromObject(obj);

		Iterator<Map.Entry<String, Object>> iterator = fromObject.entrySet().iterator();
		Map<String, Object> params = new LinkedHashMap<>();

		String[] split = clazz.toString().split("\\.");

		while (iterator.hasNext()) {
			Map.Entry<String, Object> entry = iterator.next();
			if (entry.getValue() != null && !("birthday".equals(entry.getKey()))) {
				params.put(entry.getKey(), entry.getValue());
			}
		}

		// 处理时间
		if (fromObject.containsKey("birthday")) {
			String birthdayStr = fromObject.getString("birthday");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			if (birthdayStr != null) {
				try {
					Date parse = format.parse(birthdayStr);
					params.put("birthday", parse);
					params.put("age", CommonUtil.getAge(parse));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		params.put("updateTime", new Date());
		WriteResult result = null;
		if (params.size() > 0) {
			result = marriageService.updateByParams(id, params, clazz);
		}
		return result;

	}

	@GetMapping("/getNickName")
	public String insertManyQuery() {
		String m = "天地玄黄宇宙洪荒日月盈昃辰宿列张寒来暑往秋收冬藏闰馀成岁律吕调阳云腾致雨露结为霜金生丽水玉出昆冈剑号巨阙珠称夜光果珍李柰菜重芥姜海咸河淡鳞潜羽翔龙师火帝鸟官人皇始制文字乃服衣裳推位让国有虞陶唐吊民伐罪周发殷汤坐朝问道垂拱平章爱育黎首臣伏戎羌遐迩一体率宾归王鸣凤在竹白驹食场化被草木赖及万方盖此身发四大五常恭惟鞠养岂敢毁伤女慕贞洁男效才良知过必改得能莫忘罔谈彼短靡恃己长信使可复器欲难量墨悲丝染诗赞羔羊景行维贤克念作圣德建名立形端表正空谷传声虚堂习听祸因恶积福缘善庆尺璧非宝寸阴是竞资父事君曰严与敬孝当竭力忠则尽命临深履薄夙兴温凉似兰斯馨如松之盛川流不息渊澄取映容止若思言辞安定笃初诚美慎终宜令荣业所基籍甚无竟学优登仕摄职从政存以甘棠去而益咏乐殊贵贱礼别尊卑上和下睦夫唱妇随外受傅训入奉母仪诸姑伯叔犹子比儿孔怀兄弟同气连枝交友投分切磨箴规仁慈隐恻造次弗离节义廉退颠沛匪亏性静情逸心动神疲守真志满逐物意移坚持雅操好爵自縻都邑华夏东西二京背邙面洛浮渭据泾宫殿盘郁楼观飞惊图写禽兽画彩仙灵丙舍傍启甲帐对楹肆筵设席鼓瑟吹笙升阶纳陛弁转疑星右通广内左达承明既集坟典亦聚群英杜稿钟隶漆书壁经府罗将相路侠槐卿户封八县家给千兵高冠陪辇驱毂振缨世禄侈富车驾肥轻策功茂实勒碑刻铭";
		String x = "赵钱孙李周吴郑王冯陈褚卫蒋沈韩杨朱秦尤许何吕施张孔曹严华金魏陶姜戚谢邹喻柏水窦章云苏潘葛奚范彭郎鲁韦昌马苗凤花方俞任袁柳酆鲍史唐费廉岑薛雷贺倪汤滕殷罗毕郝邬安常乐于时傅皮卞齐康伍余元卜顾孟平黄和穆萧尹姚邵湛汪祁毛禹狄米贝明臧计伏成戴谈宋茅庞熊纪舒屈项祝董梁杜阮蓝闵席季麻强贾路娄危江童颜郭梅盛林刁钟徐邱骆高夏蔡田樊胡凌霍虞万支柯昝管卢莫经房裘缪干解应宗丁宣贲邓郁单杭洪包诸左石崔吉钮龚程嵇邢滑裴陆荣翁荀羊於惠甄曲家封芮羿储靳汲邴糜松井段富巫乌焦巴弓牧隗山谷车侯宓蓬全郗班仰秋仲伊宫宁仇栾暴甘钭厉戎";
		char[] xing = x.toCharArray();
		char[] ming = m.toCharArray();

		String name = null;
		for (int i = 0; i < 10000; i++) {

			Random r = new Random();
			int xingIndex = r.nextInt(xing.length);
			name = Character.toString(xing[xingIndex]);
			for (int t = 0; t < 2; t++) {
				int mingIndex = r.nextInt(ming.length);
				name += Character.toString(ming[mingIndex]);
			}
		}
		return name;
	}

	@ApiOperation("查询发布")
	@GetMapping("/boys-search")
	public String getBoys(HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		String userId = userBo.getId();
		List<WaiterBo> list = marriageService.getBoysByUserId(userId);

		List<WaiterVo> result = new ArrayList<>();
		for (WaiterBo waiterBo : list) {
			WaiterVo waiterVo = new WaiterVo();
			BeanUtils.copyProperties(waiterBo, waiterVo);
			waiterVo.setBirthday(null);
			result.add(waiterVo);
		}
		Map map = new HashMap<>();
		map.put("ret", 0);
		map.put("publishes", result);
		return JSON.toJSONString(map);
	}

	@ApiOperation("查询发布")
	@GetMapping("/girls-search")
	public String getGirls(HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		String userId = userBo.getId();
		List<WaiterBo> list = marriageService.getGirlsByUserId(userId);

		List<WaiterVo> result = new ArrayList<>();
		for (WaiterBo waiterBo : list) {
			WaiterVo waiterVo = new WaiterVo();
			BeanUtils.copyProperties(waiterBo, waiterVo);
			waiterVo.setBirthday(null);
			result.add(waiterVo);
		}
		Map map = new HashMap<>();
		map.put("ret", 0);
		map.put("publishes", result);
		return JSON.toJSONString(map);
	}
}
