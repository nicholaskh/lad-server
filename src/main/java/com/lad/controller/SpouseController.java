package com.lad.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.lad.bo.CareAndPassBo;
import com.lad.bo.FriendsBo;
import com.lad.bo.SpouseBaseBo;
import com.lad.bo.SpouseRequireBo;
import com.lad.bo.UserBo;
import com.lad.service.CareAndPassService;
import com.lad.service.IFriendsService;
import com.lad.service.SpouseService;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.ERRORCODE;
import com.lad.vo.CareResultVo;
import com.lad.vo.ShowResultVo;
import com.lad.vo.SpouseBaseVo;
import com.lad.vo.SpouseRequireVo;
import com.mongodb.WriteResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;

@Api("老伴")
@RestController
@RequestMapping("spouse")
@SuppressWarnings("all")
public class SpouseController extends BaseContorller {
	@Autowired
	private SpouseService spouseService;

	@Autowired
	private CareAndPassService careAndPassService;

	@Autowired
	private IFriendsService friendsService;

	@GetMapping("/recommend")
	public String recommend(HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		Map map = new HashMap<>();
		// 获取自身的需求的基础id
		SpouseBaseBo spouseBaseBo = spouseService.getSpouseByUserId(userBo.getId());
		if (spouseBaseBo == null) {
			map.put("ret", -1);
			map.put("result", "当前账号无发布消息");
			return JSONObject.fromObject(map).toString();
		}
		String baseId = spouseBaseBo.getId();
		// 通过基础id获取需求
		SpouseRequireBo require = spouseService.findRequireById(baseId);
		String id = userBo.getId();
		List<Map> recommend = spouseService.getRecommend(require, id,baseId);

		map.put("ret", 0);

		Comparator<? super Map> c = new BeanComparator("match").reversed();
		recommend.sort(c);

		map.put("result", recommend);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 关键字搜索
	 * 
	 * @param keyWord
	 * @param request
	 * @param response
	 * @return
	 */
	@GetMapping("/search")
	public String search(String keyWord, int page, int limit, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		// keyWord = "邵户吹";
		SpouseBaseBo baseBo = spouseService.getSpouseByUserId(userBo.getId());

		String sex = null;
		if (baseBo != null && baseBo.getSex() != null) {
			sex = ("男".equals(baseBo.getSex())) ? "女" : "男";
		}

		List<SpouseBaseBo> list = spouseService.findListByKeyword(keyWord, sex, page, limit, SpouseBaseBo.class);

		// 遍历数据,过滤
		List<SpouseBaseVo> resultList = new ArrayList<>();
		for (SpouseBaseBo spouseBaseBo : list) {
			SpouseBaseVo baseVo = new SpouseBaseVo();
			BeanUtils.copyProperties(spouseBaseBo, baseVo);
			resultList.add(baseVo);
		}

		Map map = new HashMap<>();
		map.put("ret", 0);
		map.put("result", resultList);
		return JSON.toJSONString(map).replace("\\", "").replace("\"{", "{").replace("}\"", "}");
	}

	/**
	 * 查找当前账号下的发布信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@GetMapping("/spouse-search")
	public String getPublishById(HttpServletRequest request, HttpServletResponse response) {

		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		Map map = new HashMap<>();
		SpouseBaseBo spouseBo = spouseService.getSpouseByUserId(userBo.getId());
		
		


		
		

		if (spouseBo == null) {
			map.put("ret", -1);
			map.put("result", "当前账号无发布消息");
			return JSONObject.fromObject(map).toString().replace("\\", "").replace("\"{", "{").replace("}\"", "}");
		}

		spouseBo.setCreateTime(null);

		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		spouseBo.setBirthday(format.format(spouseBo.getBirthday()));
		map.put("baseDate", JSON.toJSONString(spouseBo));

		SpouseRequireBo requireBo = spouseService.findRequireById(spouseBo.getId());
		if (requireBo != null) {
			SpouseRequireVo requireVo = new SpouseRequireVo();
			BeanUtils.copyProperties(requireBo, requireVo);
			String age = requireVo.getAge();
			if (age.equals("17岁-100岁")) {
				age = "不限";
			} else if (age.contains("-100岁")) {
				age = age.replaceAll("-100岁", "") + "及以上";
			} else if (age.contains("17岁-")) {
				age = age.replaceAll("17岁-", "") + "及以下";
			}
			requireVo.setAge(age);
			map.put("RequireDate", requireVo);
		} else {
			map.put("RequireDate", ERRORCODE.MARRIAGE_QUIRE_NULL.getReason());
		}
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString().replace("\\", "").replace("\"{", "{").replace("}\"", "}");
	}

	/**
	 * 修改要求
	 * 
	 * @param requireData
	 * @param requireId
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation("修改要求")
	@PostMapping("/require-update")
	public String updateRequire(@RequestParam String requireData, String requireId, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (requireData == null || requireId == null) {
			return CommonUtil.toErrorResult(ERRORCODE.PARAMS_ERROR.getIndex(), ERRORCODE.PARAMS_ERROR.getReason());
		}

		updateByIdAndParams(requireData, requireId, SpouseRequireBo.class);

		return Constant.COM_RESP;
	}

	/**
	 * 修改基础资料
	 * 
	 * @param wv
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation("修改基础资料")
	@PostMapping("/base-update")
	public String updateSpouse(@RequestParam String baseData, String spouseId, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (baseData == null) {
			// 基础资料错误
			return CommonUtil.toErrorResult(ERRORCODE.MARRIAGE_WAITER_NULL.getIndex(),
					ERRORCODE.MARRIAGE_WAITER_NULL.getReason());
		}

		updateByIdAndParams(baseData, spouseId, SpouseBaseBo.class);

		return Constant.COM_RESP;
	}

	/**
	 * 取消发布
	 * 
	 * @param spouseId
	 * @param request
	 * @param response
	 * @return
	 */
	@DeleteMapping("/publish-delete")
	public String deletePublish(String spouseId, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}

		WriteResult result = spouseService.deletePublish(spouseId);

		return Constant.COM_RESP;
	}

	/**
	 * 获取最新发布的消息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@GetMapping("/new-search")
	public String getNewSpouse(int page, int limit, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}

		Map map = new HashMap<>();
		SpouseBaseBo baseBo = spouseService.getSpouseByUserId(userBo.getId());

		String sex = null;
		if (baseBo != null) {
			sex = ("男".equals(baseBo.getSex())) ? "女" : "男";
		}
		// 从后台获取数据
		List<SpouseBaseBo> list = spouseService.getNewSpouse(sex, page, limit, userBo.getId());

		// 遍历数据,过滤
		List<SpouseBaseVo> resultList = new ArrayList<>();
		for (SpouseBaseBo spouseBaseBo : list) {
			SpouseBaseVo baseVo = new SpouseBaseVo();
			BeanUtils.copyProperties(spouseBaseBo, baseVo);
			baseVo.setMyself(spouseBaseBo.getCreateuid().equals(userBo.getId()));
			resultList.add(baseVo);
		}
		map.put("ret", 0);
		map.put("result", resultList);
		return JSON.toJSONString(map).replace("\\", "").replace("\"{", "{").replace("}\"", "}");
	}

	/**
	 * 查看基础信息详细资料
	 * 
	 * @param spouseId
	 * @param request
	 * @param response
	 * @return
	 */
	@GetMapping("/base-search")
	public String getBaseById(HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}

		SpouseBaseBo baseBo2 = spouseService.getSpouseByUserId(userBo.getId());

		Map<String, Object> map = new HashMap<>();
		SpouseBaseBo baseBo = spouseService.findBaseById(baseBo2.getId());
		if (baseBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.MARRIAGE_PUBLISH_NULL.getIndex(),
					ERRORCODE.MARRIAGE_PUBLISH_NULL.getReason());
		}

		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		baseBo.setBirthday(format.format(baseBo.getBirthday()));
		map.put("ret", 0);
		SpouseBaseVo baseVo = new SpouseBaseVo();
		BeanUtils.copyProperties(baseBo, baseVo);
		map.put("baseDate", baseVo);
		return JSON.toJSONString(map).replace("\\", "").replace("\"{", "{").replace("}\"", "}");
	}

	@ApiOperation("不再推荐")
	@PostMapping("/pass")
	public String addPass(String passId, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		SpouseBaseBo baseBo = spouseService.getSpouseByUserId(userBo.getId());
		if (baseBo == null) {
			Map map = new HashMap<String, String>();
			map.put("ret", -1);
			map.put("result", "当前账号无发布消息");
			return JSONObject.fromObject(map).toString();
		}

		Set<String> passRoster = careAndPassService.findSpousePassList(baseBo.getId());

		// 如果存在这个记录
		if (passRoster != null) {
			if (!passRoster.contains(passId)) {
				passRoster.add(passId);
				Map<String, Set<String>> careMap = careAndPassService.findSpouseCareMap(baseBo.getId());
				for (Entry<String, Set<String>> entity : careMap.entrySet()) {
					if (entity.getValue().contains(passId)) {
						entity.getValue().remove(passId);
						// 多线程情况下有安全隐患
						if (entity.getValue().size() == 0) {
							careMap.remove(entity.getKey());
						}
						careAndPassService.updateCare(Constant.SPOUSE, baseBo.getId(), careMap);
						break;
					}
				}
				careAndPassService.updatePass(Constant.SPOUSE, baseBo.getId(), passRoster);
			}
		} else {
			CareAndPassBo care = new CareAndPassBo();
			// 设置主id
			care.setMainId(baseBo.getId());
			// 设置关注名单
			Map<String, Set<String>> careRoster = new HashMap<>();
			care.setCareRoster(careRoster);
			// 设置黑名单list
			Set<String> passList = new HashSet<>();
			passList.add(passId);
			care.setPassRoster(passList);
			// 设置创建者id
			care.setCreateuid(userBo.getId());
			// 设置为找驴友情境
			care.setSituation(Constant.SPOUSE);
			careAndPassService.insert(care);
		}
		Map map = new HashMap<>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@ApiOperation("查询关注列表")
	@GetMapping("/care-search")
	public String getCares(HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		// 获取当前账号发布的基本资料实体
		SpouseBaseBo baseBo = spouseService.getSpouseByUserId(userBo.getId());
		if (StringUtils.isEmpty(baseBo)) {
			Map map = new HashMap<String, String>();
			map.put("ret", -1);
			map.put("result", "当前账号无发布消息");
			return JSONObject.fromObject(map).toString();
		}

		// 在找老伴的逻辑中,主id为SpouseBaseId
		CareAndPassBo care = careAndPassService.findSpouseCare(baseBo.getId());

		// 设置装载CareResultVo的容器
		List<CareResultVo> resultContainer = new ArrayList<>();
		if (care != null) {
			Map<String, Set<String>> roster = care.getCareRoster();
			for (Entry<String, Set<String>> entity : roster.entrySet()) {
				CareResultVo result = new CareResultVo();
				result.setAddTime(entity.getKey());
				// 设置list装载遍历出的实体数据
				List<String> requireContainer = new ArrayList<>();
				// 遍历获取每一个被关注者的id
				for (String entityValue : entity.getValue()) {
					// 根据requireI遍历出require
					SpouseBaseBo requireBo = spouseService.findBaseById(entityValue);
					if (StringUtils.isEmpty(requireBo)) {
						continue;
					}
					requireBo.setCreateTime(null);

					DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					requireBo.setBirthday(format.format(requireBo.getBirthday()));
					requireContainer.add(JSON.toJSONString(requireBo));
				}
				result.setString(requireContainer);
				if (requireContainer.size() > 0) {
					resultContainer.add(result);
				}
			}
		}

		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("require", resultContainer);
		return JSON.toJSONString(map).replace("\\", "").replace("\"{", "{").replace("}\"", "}");
	}

	@ApiOperation("移除关注")
	@PostMapping("/care-delete")
	public String deleteCare(String careId, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		SpouseBaseBo baseBo = spouseService.getSpouseByUserId(userBo.getId());
		if (baseBo == null) {
			Map map = new HashMap<String, String>();
			map.put("ret", -1);
			map.put("result", "当前账号无发布消息");
			return JSONObject.fromObject(map).toString();
		}

		Map<String, Set<String>> careMap = careAndPassService.findSpouseCareMap(baseBo.getId());

		if (careMap == null) {
			careMap = new HashMap<String, Set<String>>();
		}

		for (Entry<String, Set<String>> entrySet : careMap.entrySet()) {
			if (entrySet.getValue().contains(careId)) {
				entrySet.getValue().remove(careId);
				if (entrySet.getValue().size() == 0) {
					careMap.remove(entrySet.getKey());
				}
				careAndPassService.updateCare(Constant.SPOUSE, baseBo.getId(), careMap);
				break;
			}
		}

		return Constant.COM_RESP;
	}

	@ApiOperation("添加关注")
	@PostMapping("/care-insert")
	public String addCare(String careId, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		// 获取当前账号发布的基本资料信息
		SpouseBaseBo baseBo = spouseService.getSpouseByUserId(userBo.getId());
		if (baseBo == null) {
			Map map = new HashMap<String, String>();
			map.put("ret", -1);
			map.put("result", "当前账号无发布消息");
			return JSONObject.fromObject(map).toString();
		}
		// 获取关注实体
		CareAndPassBo careAndPassBo = careAndPassService.findSpouseCare(baseBo.getId());
		// 获取当前日期的字符串表示
		String time = CommonUtil.getDateStr(new Date(), "yyyy-MM-dd");
		if (careAndPassBo != null) {
			Map<String, Set<String>> careRoster = careAndPassBo.getCareRoster();

			// 判断是否已关注该用户
			for (Entry<String, Set<String>> entity : careRoster.entrySet()) {
				if (entity.getValue().contains(careId)) {
					return "您已关注该用户";
				}
			}

			// 如果存在当天的添加记录
			if (careRoster.containsKey(time)) {
				careRoster.get(time).add(careId);
			} else {
				Set<String> careSet = new HashSet<String>();
				careSet.add(careId);
				careRoster.put(time, careSet);
			}
			careAndPassService.updateCare(Constant.SPOUSE, baseBo.getId(), careRoster);

		} else {
			CareAndPassBo care = new CareAndPassBo();
			// 设置主id
			care.setMainId(baseBo.getId());
			// 设置关注名单
			Map<String, Set<String>> careRoster = new HashMap<String, Set<String>>();
			Set<String> careSet = new HashSet();
			careSet.add(careId);
			careRoster.put(time, careSet);
			care.setCareRoster(careRoster);
			// 设置黑名单list
			Set<String> passSet = new HashSet();
			care.setPassRoster(passSet);
			// 设置创建者id
			care.setCreateuid(userBo.getId());
			// 设置为找驴友情境
			care.setSituation(Constant.SPOUSE);
			careAndPassService.insert(care);
		}

		Map map = new HashMap<>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@ApiOperation("查看详情")
	@GetMapping("/desc-search")
	public String getDescById(String spouseId, HttpServletRequest request, HttpServletResponse response) {
		// 判断当前账号是否登录
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		// 判断当前账号是否有发布消息
		/*
		 * SpouseBaseBo baseBo2 =
		 * spouseService.getSpouseByUserId(userBo.getId()); if (baseBo2 == null)
		 * { Map map = new HashMap<String, String>(); map.put("ret", -1);
		 * map.put("result", "当前账号无发布消息"); return
		 * JSONObject.fromObject(map).toString(); }
		 */

		Map<String, Object> map = new HashMap<>();
		SpouseBaseBo baseBo = spouseService.findBaseById(spouseId);
		if (baseBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.MARRIAGE_PUBLISH_NULL.getIndex(),
					ERRORCODE.MARRIAGE_PUBLISH_NULL.getReason());
		}

		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		baseBo.setBirthday(format.format(baseBo.getBirthday()));
		SpouseBaseVo baseVo = new SpouseBaseVo();
		BeanUtils.copyProperties(baseBo, baseVo);
		map.put("baseDate", baseVo);
		SpouseRequireBo requireBo = spouseService.findRequireById(spouseId);

		if (requireBo != null) {
			SpouseRequireVo requireVo = new SpouseRequireVo();
			BeanUtils.copyProperties(requireBo, requireVo);
			String age = requireVo.getAge();
			if (age.equals("17岁-100岁")) {
				age = "不限";
			} else if (age.contains("-100岁")) {
				age = age.replaceAll("-100岁", "") + "及以上";
			} else if (age.contains("17岁-")) {
				age = age.replaceAll("17岁-", "") + "及以下";
			}
			requireVo.setAge(age);
			map.put("require", requireVo);
		} else {
			map.put("require", ERRORCODE.MARRIAGE_QUIRE_NULL.getReason());
		}

		if (!(baseBo.getCreateuid().equals(userBo.getId()))) {
			ShowResultVo result = new ShowResultVo();
			result.setUid(baseBo.getCreateuid());
			FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorIdAgree(userBo.getId(), baseBo.getCreateuid());
			if (friendsBo != null) {
				result.setFriend(true);
			} else {
				result.setFriend(false);
			}
			map.put("friend", result);
		} else {
			map.put("friend", "本人");
		}

		map.put("ret", 0);
		return JSON.toJSONString(map);
	}

	@ApiOperation("发布信息")
	@PostMapping("/insert")
	public String insertPublish(@RequestParam String baseDate, @RequestParam String requireDate,
			HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);

		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}

		// 每个账户数据库只能有一条找老伴资料
		int count = spouseService.getNum(userBo.getId());
		if (count >= 1) {
			return CommonUtil.toErrorResult(ERRORCODE.SPOUSE_NUM_OUTOFLIMIT.getIndex(),
					ERRORCODE.SPOUSE_NUM_OUTOFLIMIT.getReason());
		}

		// 基本资料的实体
		JSONObject fromObject = JSONObject.fromObject(baseDate);
		SpouseBaseBo baseBo = (SpouseBaseBo) JSONObject.toBean(fromObject, SpouseBaseBo.class);
		// 初始化年龄
		String[] split = fromObject.get("birthday").toString().split("-");
		Calendar cal = Calendar.getInstance();
		cal.set(Integer.valueOf(split[0]), Integer.valueOf(split[1]), Integer.valueOf(split[2]), 0, 0, 0);
		baseBo.setBirthday(cal.getTime());
		baseBo.setAge(CommonUtil.getAge(cal.getTime()));
		// 设置创建者
		baseBo.setCreateuid(userBo.getId());

		String baseId = spouseService.insert(baseBo);

		// 设置要求的实体参数
		fromObject = JSONObject.fromObject(requireDate);
		SpouseRequireBo requireBo = (SpouseRequireBo) JSONObject.toBean(fromObject, SpouseRequireBo.class);

		// 处理生日以及年龄
		String regex = "\\D+";
		String age = requireBo.getAge();
		if (age.contains("及以上")) {
			age = age.replaceAll(regex, "岁") + "-100岁";
			requireBo.setAge(age);
		}
		if (age.contains("及以下")) {
			age = "17岁-" + age.replaceAll(regex, "岁");
			requireBo.setAge(age);
		}
		if (age.equals("不限")) {
			requireBo.setAge("17岁-100岁");
		}

		// 设置性别
		// baseBo==男 则 取女
		// baseBo==女 则 取男

		requireBo.setSex(("男".equals(baseBo.getSex())) ? "女" : "男");

		// 设置baseId
		requireBo.setBaseId(baseId);

		// 插入需求,并返回需求id
		spouseService.insert(requireBo);

		Map<String, Object> map2 = new HashMap<>();
		map2.put("ret", 0);
		map2.put("showid", baseBo.getId());
		return JSONObject.fromObject(map2).toString();
	}

	/**
	 * 私有方法,根据传入的Map集合参数修改后台数据
	 * 
	 * @param requireDate
	 * @param requireId
	 * @param clazz
	 * @return
	 */
	private WriteResult updateByIdAndParams(String requireDate, String requireId, Class clazz) {

		Iterator<Map.Entry<String, Object>> iterator = JSONObject.fromObject(requireDate).entrySet().iterator();
		Map params = new HashMap<>();
		while (iterator.hasNext()) {
			Map.Entry<String, Object> entry = iterator.next();

			if (clazz.toString().equals(SpouseRequireBo.class.toString())&& "age".equals(entry.getKey())) {
				// 处理生日以及年龄
				String regex = "\\D+";
				String age = (String) entry.getValue();
				if (age.equals("不限")) {
					params.put("age", "17岁-100岁");
				}else if (age.contains("及以上")) {
					age = age.replaceAll(regex, "岁") + "-100岁";
					params.put("age", age);
				}else if (age.contains("及以下")) {
					age = "17岁-" + age.replaceAll(regex, "岁");
					params.put("age", age);
				}else{
					params.put("age", age);
				}
				continue;
			}

			if (clazz.toString().equals(SpouseBaseBo.class.toString()) && "sex".equals(entry.getKey())) {
				Logger logger = LoggerFactory.getLogger(SpouseController.class);
				logger.error("修改找老伴基础资料");
				String sex = (String) entry.getValue();
				String requireSex = sex.equals("男") ? "女" : "男";
				WriteResult updateByParams = spouseService.updateRequireSex(requireId, requireSex,
						SpouseRequireBo.class);
			}

			if ("images".equals(entry.getKey())) {
				List<String> images = (List<String>) entry.getValue();
				if (images.size() > 4) {
					params.put("images", images.subList(0, 4));
				} else {
					params.put("images", images);
				}
				continue;
			}

			if ("birthday".equals(entry.getKey())) {
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Date birth = null;
				try {
					birth = format.parse(entry.getValue().toString());
				} catch (ParseException e) {
					e.printStackTrace();
				}
				params.put("birthday", birth);
				params.put("age", CommonUtil.getAge(birth));
				continue;
			}
			params.put(entry.getKey(), entry.getValue());
		}
		WriteResult updateByParams = spouseService.updateByParams(requireId, params, clazz);
		return updateByParams;
	}

	@GetMapping("/test")
	public void test() {
		spouseService.test();
	}
}
