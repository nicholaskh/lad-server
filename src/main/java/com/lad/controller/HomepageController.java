package com.lad.controller;

import com.lad.bo.*;
import com.lad.redis.RedisServer;
import com.lad.service.ICircleService;
import com.lad.service.IHomepageService;
import com.lad.service.IThumbsupService;
import com.lad.service.IUserService;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.ERRORCODE;
import com.lad.util.MyException;
import com.lad.vo.CircleBaseVo;
import com.lad.vo.ThumbsupVo;
import com.lad.vo.UserInfoVo;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.redisson.api.RMapCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@Scope("prototype")
@RequestMapping("homepage")
public class HomepageController extends BaseContorller {

	@Autowired
	private IHomepageService homepageService;
	@Autowired
	private IThumbsupService thumbsupService;
	@Autowired
	private IUserService userService;

	@Autowired
	private ICircleService circleService;

	@Autowired
	private RedisServer redisServer;

	@RequestMapping("/insert")
	@ResponseBody
	public String isnert(HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (session.getAttribute("isLogin") == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		userBo = userService.getUser(userBo.getId());
		Map<String, Object> map = new HashMap<String, Object>();
		HomepageBo homepageBo = new HomepageBo();
		homepageBo.setOwner_id(userBo.getId());
		homepageService.insert(homepageBo);
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/visit-my-homepage")
	@ResponseBody
	public String visit_my_homepage(String visitor_id,
			HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (session.getAttribute("isLogin") == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (!StringUtils.hasLength(visitor_id)) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CONTACT_VISITOR.getIndex(),
					ERRORCODE.CONTACT_VISITOR.getReason());
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		userBo = userService.getUser(userBo.getId());
		Map<String, Object> map = new HashMap<String, Object>();
		HomepageBo homepageBo = homepageService.selectByUserId(userBo.getId());
		if (homepageBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CONTACT_HOMEPAGE.getIndex(),
					ERRORCODE.CONTACT_HOMEPAGE.getReason());
		}
		LinkedList<String> visitor_ids = homepageBo.getVisitor_ids();
		if (visitor_ids == null) {
			visitor_ids = new LinkedList<String>();
		}
		visitor_ids.add(visitor_id);
		Integer new_visitors_count = homepageBo.getNew_visitors_count();
		if (new_visitors_count == null) {
			new_visitors_count = 0;
		}
		new_visitors_count++;
		Integer total_visitors_count = homepageBo.getNew_visitors_count();
		if (total_visitors_count == null) {
			total_visitors_count = 0;
		}
		total_visitors_count++;
		homepageBo.setNew_visitors_count(new_visitors_count);
		homepageBo.setTotal_visitors_count(total_visitors_count);
		homepageBo.setVisitor_ids(visitor_ids);
		homepageService.update_total_visitors_count(homepageBo);
		homepageService.update_new_visitors_count(homepageBo);
		homepageService.update_visitor_ids(homepageBo);
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/new-visitors-count")
	@ResponseBody
	public String new_visitors_count(HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (session.getAttribute("isLogin") == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		userBo = userService.getUser(userBo.getId());
		Map<String, Object> map = new HashMap<String, Object>();
		HomepageBo homepageBo = homepageService.selectByUserId(userBo.getId());
		if (homepageBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CONTACT_HOMEPAGE.getIndex(),
					ERRORCODE.CONTACT_HOMEPAGE.getReason());
		}
		Integer new_visitors_count = homepageBo.getNew_visitors_count();
		if (new_visitors_count == null) {
			new_visitors_count = 0;
		}
		map.put("ret", 0);
		map.put("new_visitors_count", new_visitors_count);
		homepageBo.setNew_visitors_count(0);
		homepageService.update_new_visitors_count(homepageBo);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/thumbsup")
	@ResponseBody
	public String thumbsup(String user_id, HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (session.getAttribute("isLogin") == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (!StringUtils.hasLength(user_id)) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CONTACT_VISITOR.getIndex(),
					ERRORCODE.CONTACT_VISITOR.getReason());
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CONTACT_HOMEPAGE.getIndex(),
					ERRORCODE.CONTACT_HOMEPAGE.getReason());
		}
		userBo = userService.getUser(userBo.getId());
		String owner_id = userBo.getId();
		ThumbsupBo temp = thumbsupService.getByVidAndVisitorid(owner_id,
				user_id);
		if (temp != null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CONTACT_THUMBSUP_DUPLICATE.getIndex(),
					ERRORCODE.CONTACT_THUMBSUP_DUPLICATE.getReason());
		}
		HomepageBo homepageBo = homepageService.selectByUserId(user_id);
		ThumbsupBo thumbsupBo = new ThumbsupBo();
		thumbsupBo.setOwner_id(owner_id);
		thumbsupBo.setVisitor_id(user_id);
		thumbsupBo.setHomepage_id(homepageBo.getId());
		thumbsupBo.setType(Constant.PAGE_TYPE);
		thumbsupService.insert(thumbsupBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/thumbsup-from-me")
	@ResponseBody
	public String thumbsup_from_me(String start_id, boolean gt, int limit,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (session.getAttribute("isLogin") == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		userBo = userService.getUser(userBo.getId());
		String ownerId = userBo.getId();
		List<ThumbsupBo> thumbsup_from_me = thumbsupService
				.selectByOwnerIdPaged(start_id, gt, limit, ownerId, Constant.PAGE_TYPE);
		List<ThumbsupVo> thumbsup_from_me_vo = new ArrayList<ThumbsupVo>();
		for (ThumbsupBo item : thumbsup_from_me) {
			ThumbsupVo vo = new ThumbsupVo();
			BeanUtils.copyProperties(vo, item);
			thumbsup_from_me_vo.add(vo);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("thumbsup_from_me", thumbsup_from_me_vo);

		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/thumbsup-to-me")
	@ResponseBody
	public String thumbsup_to_me(String start_id, boolean gt, int limit,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		if (session.getAttribute("isLogin") == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		userBo = userService.getUser(userBo.getId());
		String ownerId = userBo.getId();
		List<ThumbsupBo> thumbsup_to_me = thumbsupService
				.selectByVisitorIdPaged(start_id, gt, limit, ownerId, Constant.PAGE_TYPE);
		List<ThumbsupVo> thumbsup_to_me_vo = new ArrayList<ThumbsupVo>();
		for (ThumbsupBo item : thumbsup_to_me) {
			ThumbsupVo vo = new ThumbsupVo();
			BeanUtils.copyProperties(vo, item);
			thumbsup_to_me_vo.add(vo);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("thumbsup_to_me", thumbsup_to_me_vo);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/interest")
	@ResponseBody
	public String userInterest(HttpServletRequest request, HttpServletResponse response) {
		RMapCache<String, Object> cache = redisServer.getCacheMap("testCache");
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		if (cache.containsKey("interest")) {
			Object types = cache.get("interest");
			map.put("types", types);
		} else {
			Map<String, List<String>> maps = new LinkedHashMap<>();
			List<CircleTypeBo> typeBos = userService.selectByLevel(1);
			for (CircleTypeBo typeBo : typeBos) {
				List<CircleTypeBo> sonTypeBos = userService.selectByParent(typeBo.getCategory());
				List<String> sonList = new ArrayList<>();
				for (CircleTypeBo bo : sonTypeBos) {
					sonList.add(bo.getCategory());
				}
				maps.put(typeBo.getCategory(), sonList);
			}
			map.put("types", maps);
		}
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/get-interest")
	@ResponseBody
	public String getInterest(int type, HttpServletRequest request, HttpServletResponse response) {

		String tasteType = "";
		if (type == Constant.ONE) {
			tasteType = "运动";
		} else if (type == Constant.TWO) {
			tasteType = "音乐";
		} else if (type == Constant.THREE) {
			tasteType = "生活";
		} else if (type == Constant.FOUR) {
			tasteType = "旅行足迹";
		}
		List<CircleTypeBo> typeBos = userService.selectByParent(tasteType);
		List<String> interests = new ArrayList<>();
		for (CircleTypeBo typeBo : typeBos) {
			interests.add(typeBo.getCategory());
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put(tasteType, interests);
		return JSONObject.fromObject(map).toString();
	}


	@RequestMapping("/my-interest")
	@ResponseBody
	public String getMyInterest(HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		UserTasteBo tasteBo = userService.findByUserId(userBo.getId());
		if (tasteBo == null) {
			tasteBo = new UserTasteBo();
			tasteBo.setUserid(userBo.getId());
			userService.addUserTaste(tasteBo);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("sports", tasteBo.getSports());
		map.put("musics", tasteBo.getMusics());
		map.put("lifes", tasteBo.getLifes());
		map.put("trips", tasteBo.getTrips());
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/modify-interest")
	@ResponseBody
	public String addMyInterest(String interests, int type, HttpServletRequest request, HttpServletResponse
			response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		UserTasteBo tasteBo = userService.findByUserId(userBo.getId());
		LinkedHashSet<String> taste = new LinkedHashSet<>();
		String[] ins = CommonUtil.getIds(interests);
		taste.addAll(Arrays.asList(ins));
		if (tasteBo == null) {
			tasteBo = new UserTasteBo();
			tasteBo.setUserid(userBo.getId());
			userService.addUserTaste(tasteBo);
		}
		userService.updateUserTaste(tasteBo.getId(), taste, type);
		return Constant.COM_RESP;
	}



	@RequestMapping("/user-homepage")
	@ResponseBody
	public String visitUserHomepage(String userid, HttpServletRequest request, HttpServletResponse
			response) {
		UserBo loginUser = getUserLogin(request);
		UserBo userBo = userService.getUser(userid);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.USER_NULL.getIndex(),
					ERRORCODE.USER_NULL.getReason());
		}
		if (loginUser != null && !userid.equals(loginUser.getId())) {
			updateUserVisit(userid, loginUser.getId());
		}
		UserInfoVo infoVo = new UserInfoVo();
		bo2vo(userBo, infoVo);
		List<CircleBo> circleBos = circleService.findMyCircles(userBo.getId(), "", true, 4);
		List<CircleBaseVo> circles = new LinkedList<>();
		for (CircleBo circleBo : circleBos) {
			CircleBaseVo circleBaseVo = new CircleBaseVo();
			org.springframework.beans.BeanUtils.copyProperties(circleBo,circleBaseVo);
			circleBaseVo.setCircleid(circleBo.getId());
			circleBaseVo.setNotesSize(circleBo.getNoteSize());
			circleBaseVo.setUsersSize(circleBo.getTotal());
			circles.add(circleBaseVo);
		}
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("ret", 0);
		map.put("user", infoVo);
		map.put("userCricles", circles);
		return JSONObject.fromObject(map).toString();
	}


	/**
	 * 用户访问信息
	 * @param ownerid
	 * @param visitid
	 */
	@Async
	private void updateUserVisit(String ownerid, String visitid){
		UserVisitBo visitBo = userService.findUserVisit(ownerid, visitid);
		if (visitBo == null) {
			visitBo = new UserVisitBo();
			visitBo.setOwnerid(ownerid);
			visitBo.setVisitid(visitid);
			visitBo.setVisitTime(new Date());
			userService.addUserVisit(visitBo);
		} else {
			userService.updateUserVisit(visitBo.getId(), new Date());
		}
	}

	private void bo2vo(UserBo userBo, UserInfoVo infoVo){
		org.springframework.beans.BeanUtils.copyProperties(userBo, infoVo);
		UserTasteBo tasteBo = userService.findByUserId(userBo.getId());
		if (tasteBo == null) {
			tasteBo = new UserTasteBo();
			tasteBo.setUserid(userBo.getId());
			userService.addUserTaste(tasteBo);
		}
		infoVo.setSports(tasteBo.getSports());
		infoVo.setMusics(tasteBo.getMusics());
		infoVo.setLifes(tasteBo.getLifes());
		infoVo.setTrips(tasteBo.getTrips());
		infoVo.setRegistTime(userBo.getCreateTime());
	}

}
