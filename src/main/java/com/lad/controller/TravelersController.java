package com.lad.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

	// 匹配推荐
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
		if (require == null) {
			map.put("ret", -1);
			map.put("message", "请填写您的要求");
			return JSONObject.fromObject(map).toString();
		}

		List<Map> recommend = travelersService.getRecommend(require);

		if (recommend.size() >= 1) {
			List result = new ArrayList<>();

			for (Map recMap : recommend) {
				Map resultOne = new HashMap<>();
				TravelersRequireBo resultBo = (TravelersRequireBo) recMap.get("result");

				resultOne.put("match", recMap.get("match"));
				resultOne.put("baseData", getShowResultVo(userBo, resultBo));

				
				TravelersRequireVo requireVo = new TravelersRequireVo();
				BeanUtils.copyProperties(resultBo, requireVo);
				
				if (resultBo.getTimes() != null) {
					List<Date> times = resultBo.getTimes();
					DateFormat format = new SimpleDateFormat("yyyy-MM");
					String voDate = "";
					for (int i = 0; i < times.size(); i++) {
						
						if(i>=1){
							voDate+="/"+format.format(times.get(i));
						}else{
							voDate+=format.format(times.get(i));
						}
					}
					requireVo.setTimes(voDate);
				} else {
					requireVo.setTimes("");
				}
				
				resultOne.put("require", requireVo);
				result.add(resultOne);
			}
			map.put("ret", 0);
			Comparator<? super Map> c = new BeanComparator("match").reversed();
			result.sort(c);
			map.put("recommend", result);
		} else {
			map.put("ret", -1);
			map.put("message", "未找到匹配者");
		}

		return JSON.toJSONString(map).replace("\\", "").replace("\"{", "{").replace("}\"", "}");
	}

	// 关键字搜索 根据目的地搜索
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

		// 从后台搜索出符合条件的实体,遍历获取发布者信息
		for (TravelersRequireBo travelersRequireBo : list) {
			if (travelersRequireBo.getCreateuid() != null) {
				UserBo user = userService.getUser(travelersRequireBo.getCreateuid());

				ShowResultVo showResult = new ShowResultVo();

				// id不可能为null
				showResult.setId(travelersRequireBo.getId());

				if (user != null) {
					// 设置昵称
					if (user.getUserName() != null) {
						showResult.setNickName(user.getUserName());
					} else {
						showResult.setNickName("");
					}

					// 设置头像
					if (user.getHeadPictureName() != null) {
						showResult.setHeadPicture(user.getHeadPictureName());
					} else {
						showResult.setHeadPicture("");
					}

					// 设置兴趣
					UserTasteBo hobbys = userService.findByUserId(user.getId());
					if (hobbys != null) {
						hobbys.setId(null);
						hobbys.setUserid(null);
						hobbys.setUpdateTime(null);
						hobbys.setUpdateuid(null);
						hobbys.setCreateTime(null);
						hobbys.setCreateuid(null);
						showResult.setHobbys(JSON.toJSONString(hobbys));
					} else {
						showResult.setHobbys("[]");
					}
				} else {
					showResult.setErrorMsg("当前发布的发布者不存在或已注销账号");
				}

				if (travelersRequireBo.getDestination() != null) {
					showResult.setDestination(travelersRequireBo.getDestination());
				} else {
					showResult.setDestination("");
				}
				if (travelersRequireBo.getTimes() != null) {
					List<Date> times = travelersRequireBo.getTimes();
					DateFormat format = new SimpleDateFormat("yyyy-MM");
					String voDate = "";
					for (int i = 0; i < times.size(); i++) {
						
						if(i>=1){
							voDate+="/"+format.format(times.get(i));
						}else{
							voDate+=format.format(times.get(i));
						}
					}
					showResult.setTimes(voDate);
				} else {
					showResult.setTimes("");
				}
				if (travelersRequireBo.getType() != null) {
					showResult.setType(travelersRequireBo.getType());
				} else {
					showResult.setType("");
				}

				resultList.add(JSON.toJSONString(showResult));
			}
		}

		Map map = new HashMap<>();
		map.put("ret", 0);
		map.put("result", resultList);
		return JSONObject.fromObject(map).toString().replace("\\", "").replace("\"{", "{").replace("}\"", "}");
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

	@ApiOperation("修改意向资料")
	@PostMapping("/require-update")
	public String updateTravelers(@RequestParam String requireDate, String requireId, HttpServletRequest request,
			HttpServletResponse response) throws ParseException {
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
			// 如果记过不为值或者不为空
			if (next.getValue() != null && !next.getValue().toString().isEmpty()) {
				if("times".equals(next.getKey())){
					// 将传入字符串格式化为Date
					DateFormat format = new SimpleDateFormat("yyyy-MM");
					// 切割
					String[] split = fromObject.get("times").toString().split("/");
					
					List<Date> timesList = new ArrayList<>(2);
					for (int i = 0; i < 2; i++) {
						// 需要考虑到前端数据只有一个月的情况
						try {
							if(split.length==1){
								timesList.add(format.parse(split[0]));
							}else if(split.length==2){
								timesList.add(format.parse(split[i]));
							}
						} catch (ParseException e) {
							return "日期格式错误";
						}
					}
					params.put("times",timesList);
					continue;
				}
				params.put(next.getKey(), next.getValue());
			}

		}
		params.put("updateTime", new Date());
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

		if (list.size() <= 0) {
			map.put("ret", -1);
			map.put("message", "sorry,最近没有找驴友的消息发布");
			return JSONObject.fromObject(map).toString().replace("\\", "").replace("\"{", "{").replace("}\"", "}");
		}

		List resultList = new ArrayList();
		for (TravelersRequireBo travelersRequireBo : list) {
			UserBo user = userService.getUser(travelersRequireBo.getCreateuid());
			ShowResultVo showResult = new ShowResultVo();
			showResult.setId(travelersRequireBo.getId());
			if (user != null) {
				// 设置昵称
				if (user.getUserName() != null) {
					showResult.setNickName(user.getUserName());
				} else {
					showResult.setNickName("");
				}

				// 设置头像
				if (user.getHeadPictureName() != null) {
					showResult.setHeadPicture(user.getHeadPictureName());
				} else {
					showResult.setHeadPicture("");
				}

				// 设置兴趣
				UserTasteBo hobbys = userService.findByUserId(user.getId());
				if (hobbys != null) {
					hobbys.setId(null);
					hobbys.setUserid(null);
					hobbys.setUpdateTime(null);
					hobbys.setUpdateuid(null);
					hobbys.setCreateTime(null);
					hobbys.setCreateuid(null);
					showResult.setHobbys(JSON.toJSONString(hobbys));
				} else {
					showResult.setHobbys("[]");
				}

			} else {
				showResult.setErrorMsg("当前发布的发布者不存在或已注销账号");
			}

			if (travelersRequireBo.getDestination() != null) {
				showResult.setDestination(travelersRequireBo.getDestination());
			} else {
				showResult.setDestination("");
			}
			if (travelersRequireBo.getTimes() != null) {
				List<Date> times = travelersRequireBo.getTimes();
				DateFormat format = new SimpleDateFormat("yyyy-MM");
				String voDate = "";
				for (int i = 0; i < times.size(); i++) {
					
					if(i>=1){
						voDate+="/"+format.format(times.get(i));
					}else{
						voDate+=format.format(times.get(i));
					}
				}
				showResult.setTimes(voDate);
			} else {
				showResult.setTimes("");
			}
			if (travelersRequireBo.getType() != null) {
				showResult.setType(travelersRequireBo.getType());
			} else {
				showResult.setType("");
			}
			resultList.add(JSON.toJSONString(showResult));
		}
		map.put("ret", 0);
		map.put("result", resultList);
		return JSONObject.fromObject(map).toString().replace("\\", "").replace("\"{", "{").replace("}\"", "}");
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
			ShowResultVo showResult = getShowResultVo(userBo, requireBo);

			map.put("ret", 0);
			map.put("result", showResult);
			
			TravelersRequireVo travelersRequireVo = new TravelersRequireVo();
			
			DateFormat format = new SimpleDateFormat("yyyy-MM");
			List<Date> timesArr = requireBo.getTimes();
			// 判断是否过期
			long endTime = Long.valueOf(format.format(timesArr.get(timesArr.size()-1)).replaceAll("-", ""));			
			long nowTime = Long.valueOf(format.format(new Date()).replaceAll("-", ""));
			travelersRequireVo.setExpired(nowTime>endTime?1:0);
			
			BeanUtils.copyProperties(requireBo, travelersRequireVo);
			// 设置返回时间
			String voDate = "";
			for (int i = 0; i < timesArr.size(); i++) {
				if(i>=1){
					voDate+="/"+format.format(timesArr.get(i));
				}else{
					voDate+=format.format(timesArr.get(i));
				}
			}
			travelersRequireVo.setTimes(voDate);

			map.put("require", JSON.toJSONString(travelersRequireVo));
		} else {
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


		Map map = new HashMap<>();

		// 用户要求
		List<TravelersRequireBo> requires = travelersService.getRequireList(userBo.getId());
		List<TravelersRequireVo> list = new ArrayList<>();
		if (requires.size() <= 0) {
			map.put("ret", -1);
			map.put("message", "当前账号无发布旅游需求");
			return JSONObject.fromObject(map).toString().replace("\\", "").replace("\"{", "{").replace("}\"", "}");
		}

		for (TravelersRequireBo travelersRequireBo : requires) {
			TravelersRequireVo travelersRequireVo = new TravelersRequireVo();
			DateFormat format = new SimpleDateFormat("yyyy-MM");
			// 获取最后时间
			List<Date> timesArr = travelersRequireBo.getTimes();
			long endTime = Long.valueOf(format.format(timesArr.get(timesArr.size()-1)).replaceAll("-", ""));
			// 现在时间
			
			long nowTime = Long.valueOf(format.format(new Date()).replaceAll("-", ""));
			travelersRequireVo.setExpired(nowTime>endTime?1:0);
			
			BeanUtils.copyProperties(travelersRequireBo, travelersRequireVo);
			list.add(travelersRequireVo);
		}

		// 用户兴趣,字段过滤
		UserTasteBo hobbys = userService.findByUserId(userBo.getId());
		if (hobbys != null) {
			hobbys.setId(null);
			hobbys.setUserid(null);
			hobbys.setUpdateTime(null);
			hobbys.setUpdateuid(null);
			hobbys.setCreateTime(null);
			
			
		}

		map.put("ret", 0);
		map.put("nickName", userBo.getUserName());
		map.put("hobbys", JSON.toJSONString(hobbys));
		map.put("result", list);
		map.put("headPictureName", userBo.getHeadPictureName());
		
		return JSON.toJSONString(map).replace("\\", "").replace("\"{", "{").replace("}\"", "}");
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


		JSONObject fromObject = JSONObject.fromObject(requireDate);
		TravelersRequireVo requireVo = (TravelersRequireVo) JSONObject.toBean(fromObject, TravelersRequireVo.class);
		TravelersRequireBo requireBo = new TravelersRequireBo();
		BeanUtils.copyProperties(requireVo, requireBo);
		
		// 设置出发时段
		
		// 将传入字符串格式化为Date
		DateFormat format = new SimpleDateFormat("yyyy-MM");
		// 切割
		String[] split = fromObject.get("times").toString().split("/");
		
		List<Date> timesList = new ArrayList<>(2);
		for (int i = 0; i < 2; i++) {
			// 需要考虑到前端数据只有一个月的情况
			try {
				if(split.length==1){
					timesList.add(format.parse(split[0]));
				}else if(split.length==2){
					timesList.add(format.parse(split[i]));
				}
			} catch (ParseException e) {
				return "日期格式错误";
			}
		}

		requireBo.setTimes(timesList);

		// 插入需求,并返回需求id
		requireBo.setCreateuid(userBo.getId());

		if (requireBo.getImages() == null) {
			requireBo.setImages(new ArrayList<String>());
		}

		travelersService.insert(requireBo);

		Map<String, Object> map2 = new HashMap<>();
		map2.put("ret", 0);
		map2.put("requireId", requireBo.getId());
		return JSONObject.fromObject(map2).toString();
	}

	private ShowResultVo getShowResultVo(UserBo userBo, TravelersRequireBo requireBo) {

		ShowResultVo showResult = new ShowResultVo();

		if (requireBo.getCreateuid() != null) {
			// 编辑基础资料字段
			UserBo user = userService.getUser(requireBo.getCreateuid());

			if (user != null) {
				// 设置头像
				if (user.getHeadPictureName() != null) {
					showResult.setHeadPicture(user.getHeadPictureName());
				} else {
					showResult.setHeadPicture("");
				}
				// 设置昵称
				if (user.getUserName() != null) {
					showResult.setNickName(user.getUserName());
				} else {
					showResult.setNickName("");
				}
				// 设置性别
				if (user.getSex() != null) {
					showResult.setSex(user.getSex());
				} else {
					showResult.setSex("");
				}

				// 设置年龄
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				format = new SimpleDateFormat("yyyy年MM月dd日");
				Date birth = null;
				try {
					if (user.getBirthDay() != null) {
						birth = format.parse(user.getBirthDay());
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (birth != null) {
					showResult.setAge(CommonUtil.getAge(birth));
				} else {
					showResult.setAge(0);
				}

				if (!(user.getId().equals(userBo.getId()))) {
					showResult.setUid(user.getId());
					FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorIdAgree(userBo.getId(), user.getId());
					if (friendsBo != null) {
						showResult.setFriend(true);
					} else {
						showResult.setFriend(false);
					}
				}

				// 设置居住地
				if (user.getCity() != null) {
					showResult.setAddress(user.getCity());
				} else {
					showResult.setAddress("");
				}

				// 设置兴趣
				UserTasteBo hobbys = userService.findByUserId(user.getId());
				if (hobbys != null) {
					hobbys.setId(null);
					hobbys.setUserid(null);
					
					hobbys.setUpdateTime(null);
					hobbys.setUpdateuid(null);
					hobbys.setCreateTime(null);
					hobbys.setCreateuid(null);
					showResult.setHobbys(JSON.toJSONString(hobbys));
				} else {
					showResult.setHobbys(new ArrayList<>());
				}

			} else {
				showResult.setErrorMsg("当前发布的发布者不存在或已注销账号");
			}

		}

		// 设置照片
		if (requireBo.getImages() != null) {
			showResult.setImages(requireBo.getImages());
		} else {
			showResult.setImages(new ArrayList());
		}

		return showResult;
	}

	@GetMapping("/test")
	public void test() {
		travelersService.test();
	}

	@ApiOperation("添加黑名单")
	@Deprecated
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
	@Deprecated
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
	@Deprecated
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
	@Deprecated
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
}
