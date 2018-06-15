package com.lad.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.lad.bo.CareAndPassBo;
import com.lad.bo.FriendsBo;
import com.lad.bo.TravelersRequireBo;
import com.lad.bo.UserBo;
import com.lad.bo.UserTasteBo;
import com.lad.service.CareAndPassService;
import com.lad.service.IFriendsService;
import com.lad.service.IUserService;
import com.lad.service.TravelersService;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.ERRORCODE;
import com.lad.vo.CareResultVo;
import com.lad.vo.ShowResultVo;
import com.lad.vo.TravelersRequireVo;
import com.mongodb.WriteResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;

@Api("驴友接口")
@RestController
@RequestMapping("travelers")
@SuppressWarnings("all")
public class TravelersController extends BaseContorller {
	@Autowired
	private TravelersService travelersService;

	@Autowired
	private IUserService userService;

	@Autowired
	private CareAndPassService careAndPassService;
	
	@Autowired
	private IFriendsService friendsService;

	@GetMapping("/recommend")
	public String recommend(String requireId, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		Map map = new HashMap<>();
		// 通过基础id获取需求
		TravelersRequireBo require = travelersService.getRequireById(requireId);
		if(require==null){
			map.put("ret", -1);
			map.put("message", "请填写您的要求");
			return JSONObject.fromObject(map).toString();
		}

		List<Map> recommend = travelersService.getRecommend(require);
		
		List result = new ArrayList<>();
		if(recommend.size()>=1){
			
			Map resultOne = new HashMap<>();
			for (Map recMap : recommend) {
				TravelersRequireBo resultBo = (TravelersRequireBo)recMap.get("result");
				ShowResultVo showResultVo = getShowResultVo(userBo,resultBo);
				resultOne.put("match", recMap.get("match"));
				resultOne.put("baseData", showResultVo);
				resultOne.put("require", resultBo);
				result.add(resultOne);
			}
			
			
			
			map.put("ret", 0);
			map.put("recommend", result);
		}else{
			map.put("ret",-1);
			map.put("message", "未找到匹配者");
		}

		return JSONObject.fromObject(map).toString();
	}

	@GetMapping("/search")
	public String search(String keyWord, int page, int limit, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}

		List<TravelersRequireBo> list = travelersService.findListByKeyword(keyWord, page, limit,
				TravelersRequireBo.class);
		List resultList = new ArrayList();
		for (TravelersRequireBo travelersRequireBo : list) {
			UserBo user = userService.getUser(travelersRequireBo.getCreateuid());
			ShowResultVo showResult = new ShowResultVo();
			showResult.setId(travelersRequireBo.getId());
			// 设置昵称
			showResult.setNickName(user.getUserName());
			// 设置头像
			showResult.setHeadPicture(user.getHeadPictureName());
			// 设置兴趣
			UserTasteBo hobbys = userService.findByUserId(user.getId());
			if (hobbys != null) {
				hobbys.setId(null);
				hobbys.setUserid(null);
				hobbys.setDeleted(null);
				hobbys.setUpdateTime(null);
				hobbys.setUpdateuid(null);
				hobbys.setCreateTime(null);
			}
			showResult.setHobbys(JSON.toJSONString(hobbys));

			showResult.setDestination(travelersRequireBo.getDestination());
			showResult.setDays(travelersRequireBo.getDays());
			showResult.setType(travelersRequireBo.getType());

			resultList.add(showResult);
		}

		Map map = new HashMap<>();
		map.put("ret", 0);
		map.put("result", resultList);
		return JSONObject.fromObject(map).toString();
	}

	@PostMapping("/delete")
	public String deletePublish(String requireId, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}

		WriteResult result = travelersService.deletePublish(requireId);

		return Constant.COM_RESP;
	}

	@ApiOperation("修改基础资料")
	@PostMapping("/require-update")
	public String updateTravelers(@RequestParam String requireDate, String requireId, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (requireDate == null) {
			// 基础资料错误
			return CommonUtil.toErrorResult(ERRORCODE.MARRIAGE_WAITER_NULL.getIndex(),
					ERRORCODE.MARRIAGE_WAITER_NULL.getReason());
		}

		JSONObject fromObject = JSONObject.fromObject(requireDate);
		Set<Map.Entry<String, Object>> entitySet = fromObject.entrySet();
		Iterator<Map.Entry<String, Object>> iterator = entitySet.iterator();

		Map<String, Object> params = new HashMap<>();

		while (iterator.hasNext()) {
			Entry<String, Object> next = iterator.next();
			params.put(next.getKey(), next.getValue());
		}

		travelersService.updateByIdAndParams(requireId, params);

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
	public String getNewTravelers(int page, int limit, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}

		Map map = new HashMap<>();

		// 从后台获取数据
		List<TravelersRequireBo> list = travelersService.getNewTravelers(page, limit, userBo.getId());

		if(list.size()<=0){
			map.put("ret", -1);
			map.put("message", "sorry,最近没有找驴友的消息发布");
			return JSONObject.fromObject(map).toString().replace("\\", "").replace("\"{", "{").replace("}\"", "}");
		}
		
		List resultList = new ArrayList();
		for (TravelersRequireBo travelersRequireBo : list) {
			UserBo user = userService.getUser(travelersRequireBo.getCreateuid());
			ShowResultVo showResult = new ShowResultVo();
			showResult.setId(travelersRequireBo.getId());
			// 设置昵称
			showResult.setNickName(user.getUserName());
			// 设置头像
			showResult.setHeadPicture(user.getHeadPictureName());
			// 设置兴趣
			UserTasteBo hobbys = userService.findByUserId(user.getId());
			if (hobbys != null) {
				hobbys.setId(null);
				hobbys.setUserid(null);
				hobbys.setDeleted(null);
				hobbys.setUpdateTime(null);
				hobbys.setUpdateuid(null);
				hobbys.setCreateTime(null);
				hobbys.setCreateuid(null);
			}
			showResult.setHobbys(JSON.toJSONString(hobbys));

			showResult.setDestination(travelersRequireBo.getDestination());
			showResult.setDays(travelersRequireBo.getDays());
			showResult.setType(travelersRequireBo.getType());

			resultList.add(showResult);
		}
		map.put("ret", 0);
		map.put("result", resultList);
		return JSONObject.fromObject(map).toString().replace("\\", "").replace("\"{", "{").replace("}\"", "}");
	}

	@ApiOperation("添加黑名单")
	@PostMapping("/addPass")
	public String addPass(String requireId, String passId, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}

		List<String> passRoster = careAndPassService.findTravelersPassList(requireId);

		// 如果存在这个记录
		if (passRoster != null) {
			if (!passRoster.contains(passId)) {
				passRoster.add(passId);
				Map<String, List<String>> careMap = careAndPassService.findTravelersCareMap(requireId);
				for (Entry<String, List<String>> entity : careMap.entrySet()) {
					if (entity.getValue().contains(passId)) {
						entity.getValue().remove(passId);
						// 多线程情况下有安全隐患
						if (entity.getValue().size() == 0) {
							careMap.remove(entity.getKey());
						}
						careAndPassService.updateCare(Constant.TRAVELERS, requireId, careMap);
						break;
					}
				}
				careAndPassService.updatePass(Constant.TRAVELERS, requireId, passRoster);
			}
		} else {
			CareAndPassBo care = new CareAndPassBo();
			// 设置主id
			care.setMainId(requireId);
			// 设置关注名单
			Map<String, List<String>> careRoster = new HashMap<>();
			care.setCareRoster(careRoster);
			// 设置黑名单list
			List<String> passList = new ArrayList<String>();
			passList.add(passId);
			care.setPassRoster(passList);
			// 设置创建者id
			care.setCreateuid(userBo.getId());
			// 设置为找驴友情境
			care.setSituation(Constant.TRAVELERS);
			careAndPassService.insert(care);
		}
		Map map = new HashMap<>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	// 移除关注
	@ApiOperation("移除关注")
	@PostMapping("/removeCare")
	public String deleteCare(String requireId, String careId, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}

		Map<String, List<String>> careMap = careAndPassService.findTravelersCareMap(requireId);

		if (careMap == null) {
			careMap = new HashMap<String, List<String>>();
		}

		for (Entry<String, List<String>> entrySet : careMap.entrySet()) {
			if (entrySet.getValue().contains(careId)) {
				entrySet.getValue().remove(careId);
				if (entrySet.getValue().size() == 0) {
					careMap.remove(entrySet.getKey());
				}
				careAndPassService.updateCare(Constant.TRAVELERS, requireId, careMap);
				break;
			}
		}

		return Constant.COM_RESP;
	}

	@ApiOperation("查看关注列表")
	@GetMapping("/getCare")
	public String getCare(String requireId, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		// 在找驴友的逻辑中,主id即requireId
		CareAndPassBo care = careAndPassService.findTravelersCare(requireId);

		// 设置壮哉CareResultVo的容器
		List<CareResultVo> resultContainer = new ArrayList<>();
		if (care != null) {
			Map<String, List<String>> roster = care.getCareRoster();
			CareResultVo result = new CareResultVo();
			for (Entry<String, List<String>> entity : roster.entrySet()) {
				result.setAddTime(entity.getKey());
				// 设置list装载遍历出的实体数据
				List<String> requireContainer = new ArrayList<>();
				for (String entityValue : entity.getValue()) {
					// 根据requireI遍历出require
					TravelersRequireBo requireBo = travelersService.getRequireById(entityValue);
					requireBo.setCreateTime(null);
					requireBo.setDeleted(null);

					DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					requireBo.setAssembleTime(format.format(requireBo.getAssembleTime()));
					requireContainer.add(JSON.toJSONString(requireBo));
				}
				result.setString(requireContainer);
				resultContainer.add(result);
			}
		}

		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("require", resultContainer);
		return JSONObject.fromObject(map).toString();
	}

	@ApiOperation("添加关注")
	@PostMapping("/addCare")
	public String addCare(String requireId, String careId, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}

		CareAndPassBo careAndPassBo = careAndPassService.findTravelersCare(requireId);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String time = format.format(new Date());

		if (careAndPassBo != null) {
			Map<String, List<String>> careRoster = careAndPassBo.getCareRoster();

			// 判断是否已关注该用户
			for (Entry<String, List<String>> entity : careRoster.entrySet()) {
				if (entity.getValue().contains(careId)) {
					return "您已关注该用户";
				}
			}

			// 如果存在当天的添加记录
			if (careRoster.containsKey(time)) {
				careRoster.get(time).add(careId);
			} else {
				List<String> careList = new ArrayList<String>();
				careList.add(careId);
				careRoster.put(time, careList);
			}
			careAndPassService.updateCare(Constant.TRAVELERS, requireId, careRoster);

		} else {
			CareAndPassBo care = new CareAndPassBo();
			// 设置主id
			care.setMainId(requireId);
			// 设置关注名单
			Map<String, List<String>> careRoster = new HashMap<>();
			List<String> careList = new ArrayList<String>();
			careList.add(careId);
			careRoster.put(time, careList);
			care.setCareRoster(careRoster);
			// 设置黑名单list
			List<String> passList = new ArrayList<String>();
			care.setPassRoster(passList);
			// 设置创建者id
			care.setCreateuid(userBo.getId());
			// 设置为找驴友情境
			care.setSituation(Constant.TRAVELERS);
			careAndPassService.insert(care);
		}

		Map map = new HashMap<>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@ApiOperation("查询一条发布的发布详情")
	@GetMapping("/desc-search")
	public String getPublishDescById(String requireId, HttpServletRequest request, HttpServletResponse response) {

		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		
		// 编辑需求字段
		TravelersRequireBo requireBo = travelersService.getRequireById(requireId);
		Map<String, Object> map = new HashMap<>();
		
		if (requireBo != null) {
			ShowResultVo showResult = getShowResultVo(userBo,requireBo);

			// showResult.setRequire(JSON.toJSONString(requireBo));

			map.put("ret", 0);
			map.put("result", showResult);
			requireBo.setDeleted(null);
			requireBo.setCreateTime(null);
			map.put("require", JSON.toJSONString(requireBo));
		}else{
			map.put("ret", -1);
			map.put("message", "id错误");
		}
		return JSON.toJSONString(map).replace("\\", "").replace("\"{", "{").replace("}\"", "}");
	}



	/**
	 * 查询当前账户下的发布信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@GetMapping("/travelers-search")
	public String getPublishById(HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}

		List<String> list = new ArrayList<>();
		Map map = new HashMap<>();

		// 用户要求
		List<TravelersRequireBo> requires = travelersService.getRequireList(userBo.getId());
		if(requires.size()<=0){
			map.put("ret", -1);
			map.put("message", "当前账号无发布旅游需求");
			return JSONObject.fromObject(map).toString().replace("\\", "").replace("\"{", "{").replace("}\"", "}");
		}

		for (TravelersRequireBo travelersRequireBo : requires) {
			travelersRequireBo.setDeleted(null);
			travelersRequireBo.setCreateTime(null);
			list.add((JSON.toJSONString(travelersRequireBo)));
		}

		// 用户兴趣,字段过滤
		UserTasteBo hobbys = userService.findByUserId(userBo.getId());
		if (hobbys != null) {
			hobbys.setId(null);
			hobbys.setUserid(null);
			hobbys.setDeleted(null);
			hobbys.setUpdateTime(null);
			hobbys.setUpdateuid(null);
			hobbys.setCreateTime(null);
		}

		map.put("ret", 0);
		map.put("nickName", userBo.getUserName());
		map.put("hobbys", JSON.toJSONString(hobbys));
		map.put("result", list);
		map.put("headPictureName", userBo.getHeadPictureName());
		return JSONObject.fromObject(map).toString().replace("\\", "").replace("\"{", "{").replace("}\"", "}");
	}

	/**
	 * 添加发布
	 * 
	 * @param baseDate
	 * @param requireDate
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation("发布信息")
	@PostMapping("/insert")
	public String insertPublish(@RequestParam String requireDate, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);

		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}

		TravelersRequireVo requireVo = null;
		try {
			JSONObject fromObject = JSONObject.fromObject(requireDate);
			requireVo = (TravelersRequireVo) JSONObject.toBean(fromObject, TravelersRequireVo.class);
		} catch (Exception e) {
			e.printStackTrace();
			return e.toString();
		}

		/* =================存储要求======================= */
		// 设置要求的实体参数
		TravelersRequireBo requireBo = new TravelersRequireBo();
		BeanUtils.copyProperties(requireVo, requireBo);

		// 设置集合时间
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date parse = null;
		try {
			parse = format.parse(requireVo.getAssembleTime());

		} catch (ParseException e) {
			e.printStackTrace();
		}
		requireBo.setAssembleTime(parse);
		// 插入需求,并返回需求id
		requireBo.setCreateuid(userBo.getId());

		if (requireBo.getImages() == null) {
			List<String> list = new ArrayList<String>();
			requireBo.setImages(list);
		}

		travelersService.insert(requireBo);

		Map<String, Object> map2 = new HashMap<>();
		map2.put("ret", 0);
		map2.put("requireId", requireBo.getId());
		return JSONObject.fromObject(map2).toString();
	}
	
	private ShowResultVo getShowResultVo(UserBo userBo,TravelersRequireBo requireBo) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		requireBo.setAssembleTime(format.format(requireBo.getAssembleTime()));

		// 编辑基础资料字段
		UserBo user = userService.getUser(requireBo.getCreateuid());
		ShowResultVo showResult = new ShowResultVo();
		// 设置头像
		showResult.setHeadPicture(user.getHeadPictureName());
		// 设置昵称
		showResult.setNickName(user.getUserName());
		// 设置性别
		showResult.setSex(user.getSex());
		// 设置年龄
		format = new SimpleDateFormat("yyyy年MM月dd日");
		Date birth = null;
		try {
			if(user.getBirthDay()!=null){
				birth = format.parse(user.getBirthDay());
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (birth != null) {
			showResult.setAge(CommonUtil.getAge(birth));
		}

		
		if(!(user.getId().equals(userBo.getId()))){
			showResult.setUid(user.getId());
			FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorIdAgree(userBo.getId(),user.getId());
			if(friendsBo!=null){
				showResult.setFriend(true);
			}else{
				showResult.setFriend(false);
			}
		}
		
		
		// 设置居住地
		showResult.setAddress(user.getCity());

		// 设置兴趣
		UserTasteBo hobbys = userService.findByUserId(user.getId());
		if (hobbys != null) {
			hobbys.setId(null);
			hobbys.setUserid(null);
			hobbys.setDeleted(null);
			hobbys.setUpdateTime(null);
			hobbys.setUpdateuid(null);
			hobbys.setCreateTime(null);
			hobbys.setCreateuid(null);
		}
		showResult.setHobbys(JSON.toJSONString(hobbys));
		// 设置照片
		List images = new ArrayList();
		if(requireBo.getImages()!=null){
			images = requireBo.getImages();
		}
		showResult.setImages(requireBo.getImages());
		return showResult;
	}

	@GetMapping("/test")
	public void test() {
		travelersService.test();
	}
}
