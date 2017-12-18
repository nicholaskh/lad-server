package com.lad.controller;

import com.lad.bo.*;
import com.lad.redis.RedisServer;
import com.lad.service.*;
import com.lad.util.*;
import com.lad.vo.*;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("circle")
public class CircleController extends BaseContorller {


	private static Logger logger = LogManager.getLogger(ChatroomController.class);

	@Autowired
	private ICircleService circleService;

	@Autowired
	private IUserService userService;

	@Autowired
	private ILocationService locationService;

	@Autowired
	private INoteService noteService;

	@Autowired
	private IFeedbackService feedbackService;

	@Autowired
	private ISearchService searchService;

	@Autowired
	private RedisServer redisServer;

	@Autowired
	private IFriendsService friendsService;

	@Autowired
	private IReasonService reasonService;

	@Autowired
	private IPartyService partyService;

	@Autowired
	private IDynamicService dynamicService;

	@Autowired
	private ICollectService collectService;

	private String titlePush = "圈子通知";

	/**
	 * 圈子添加成员时锁
	 */
	private String circleAddUserLock = "circleAdd";

	@RequestMapping("/insert")
	@ResponseBody
	public String isnert(@RequestParam(required = true) double px,
						 @RequestParam(required = true) double py,
						 @RequestParam(required = true) String name,
						 @RequestParam(required = true) String tag,
						 @RequestParam(required = true) String sub_tag,
						 String description, String province, String city,
						 String district, boolean isOpen, MultipartFile head_picture,
			HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		//每人最多创建三个群
		long circleNum = circleService.findCreateCricles(userBo.getId());
		if (circleNum >= userBo.getLevel() * 5) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_CREATE_MAX.getIndex(),
					ERRORCODE.CIRCLE_CREATE_MAX.getReason());
		}
		//是否存在相同名字圈子
		CircleBo circle = circleService.findByTagAndName(name, tag, sub_tag);
		if (circle != null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_NAME_EXIST.getIndex(),
					ERRORCODE.CIRCLE_NAME_EXIST.getReason());
		}
		//圈子名称不能和分类名称一样
		CircleTypeBo typeBo = circleService.findEsixtTagName(name);
		if (null != typeBo) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_NAME_EXIST.getIndex(),
					ERRORCODE.CIRCLE_NAME_EXIST.getReason());
		}

		CircleBo circleBo = new CircleBo();
		circleBo.setCreateuid(userBo.getId());
		circleBo.setPosition(new double[] { px, py });
		circleBo.setName(name);
		circleBo.setTag(tag);
		circleBo.setUsernum(1);
		circleBo.setSub_tag(sub_tag);
		circleBo.setDescription(description);
		circleBo.setOpen(isOpen);
		circleBo.setProvince(province);
		circleBo.setCity(city);
		circleBo.setDistrict(district);
		String userId = userBo.getId();
		//圈子头像
		if (head_picture != null){
			long time = Calendar.getInstance().getTimeInMillis();
			String fileName = String.format("%s-%d-%s", userId, time, head_picture.getOriginalFilename());
			String path = CommonUtil.upload(head_picture,
					Constant.CIRCLE_HEAD_PICTURE_PATH, fileName, 0);
			circleBo.setHeadPicture(path);
			logger.info("circle create headPic : {}", path);
		}
		HashSet<String> users = new HashSet<String>();
		users.add(userBo.getId());
		circleBo.setUsers(users);
		circleService.insert(circleBo);

		CircleAddBo addBo = new CircleAddBo();
		addBo.setUserid(userId);
		addBo.setCircleid(circleBo.getId());
		addBo.setStatus(1);
		circleService.insertCircleAdd(addBo);

		userService.addUserLevel(userBo.getId(), 1, Constant.LEVEL_CIRCLE);
		updateHistory(userBo.getId(), circleBo.getId(), locationService, circleService);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("circleid", circleBo.getId());
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/pre-create")
	@ResponseBody
	public String preCreateCircle(HttpServletRequest request, HttpServletResponse response){
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		List<CircleBo> circleBos = circleService.findByCreateid(userBo.getId());
		int initNum = 5 * userBo.getLevel();
		int userLevel = userBo.getLevel();
		int createNum = 0;
		if (circleBos != null) {
			createNum = circleBos.size();
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("userLevel", userLevel);
		map.put("createNum", createNum);
		map.put("maxNum", initNum);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/check-name")
	@ResponseBody
	public String preCreateCircle(String name, String tag, String sub_tag, HttpServletRequest request,
								  HttpServletResponse response){
		CircleBo circleBo = circleService.findByTagAndName(name, tag, sub_tag);
		if (circleBo != null) {
			return "{\"ret\":1}";
		} else {
			return Constant.COM_RESP;
		}
	}


	@RequestMapping("/head-picture")
	@ResponseBody
	public String head_picture(
			@RequestParam("head_picture") MultipartFile file,
			@RequestParam(required = true) String circleid,
			HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		String userId = userBo.getId();
		long time = Calendar.getInstance().getTimeInMillis();
		String fileName = String.format("%s-%d-%s", userId, time, file.getOriginalFilename());
		String path = CommonUtil.upload(file,
				Constant.CIRCLE_HEAD_PICTURE_PATH, fileName, 0);
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		if (!circleBo.getMasters().contains(userId) && !circleBo.getCreateuid().equals(userId)){
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_NOT_MASTER.getIndex(),
					ERRORCODE.CIRCLE_NOT_MASTER.getReason());
		}
		circleService.updateHeadPicture(circleid, path);
		logger.info("circle headPic update  {} ,  user  {}, headPic {} : ", circleid, userId, path);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("path", path);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/apply-insert")
	@ResponseBody
	public String applyIsnert(@RequestParam(required = true) String circleid, String reason, boolean isNotice,
			HttpServletRequest request, HttpServletResponse response) {
		return applyIsnert(circleid, reason, isNotice,0, null, request, response);
	}

	@RequestMapping("/party-apply-insert")
	@ResponseBody
	public String applyIsnert(@RequestParam(required = true) String circleid, String reason, boolean isNotice, int
			addType, String partyid, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		updateHistory(userBo.getId(), circleid, locationService, circleService);
		HashSet<String> usersApply = circleBo.getUsersApply();
		if (usersApply.contains(userBo.getId()) || circleBo.getUsers().contains(userBo.getId())) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_USER_EXIST.getIndex(),
					ERRORCODE.CIRCLE_USER_EXIST.getReason());
		}

		ReasonBo reasonBo = reasonService.findByUserAdd(userBo.getId(), circleid);
		if (reasonBo == null) {
			reasonBo = new ReasonBo();
			reasonBo.setCircleid(circleid);
			reasonBo.setReason(reason);
			reasonBo.setNotice(isNotice);
			if (StringUtils.isNotEmpty(partyid)) {
				reasonBo.setAddType(addType);
				reasonBo.setPartyid(partyid);
			}
			reasonBo.setCreateuid(userBo.getId());
			reasonBo.setStatus(Constant.ADD_APPLY);
			reasonService.insert(reasonBo);
		} else if (reasonBo.getStatus() == Constant.ADD_AGREE) {
			HashSet<String> users = circleBo.getUsers();
			users.add(userBo.getId());
			circleAddUsers(circleid, users);
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_USER_EXIST.getIndex(),
					ERRORCODE.CIRCLE_USER_EXIST.getReason());
		} else {
			reasonService.updateApply(reasonBo.getId(), Constant.ADD_APPLY, reason);
		}
		usersApply.add(userBo.getId());
		circleAddUserApply(circleid, usersApply);
		String content = String.format("“%s”申请加入您的圈子【%s】，快去审核吧", userBo.getUserName(),
				circleBo.getName());
		String path = "/circle/user-apply.do?circleid=" + circleid;
		JPushUtil.push(titlePush, content, path,  circleBo.getCreateuid());
		HashSet<String> masters = circleBo.getMasters();
		if (!CommonUtil.isEmpty(masters)) {
			String[] pushUser = new String[masters.size()];
			masters.toArray(pushUser);
			JPushUtil.push(titlePush, content, path,  pushUser);
		}
		return Constant.COM_RESP;
	}



	@RequestMapping("/free-insert")
	@ResponseBody
	public String applyIsnert(@RequestParam(required = true) String circleid,
							  HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		if (circleBo.getUsers().contains(userBo.getId())) {
			return Constant.COM_RESP;
		}
		if (!circleBo.isOpen() || circleBo.isVerify()) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_NEED_VERIFY.getIndex(),
					ERRORCODE.CIRCLE_NEED_VERIFY.getReason());
		}
		updateHistory(userBo.getId(), circleid, locationService, circleService);
		HashSet<String> users= circleBo.getUsers();
		if (users.size() + 1 >= 500) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_USER_MAX.getIndex(),
					ERRORCODE.CIRCLE_USER_MAX.getReason());
		}
		ReasonBo reasonBo = reasonService.findByUserAdd(userBo.getId(), circleid);
		if (reasonBo == null) {
			reasonBo = new ReasonBo();
			reasonBo.setCircleid(circleid);
			reasonBo.setCreateuid(userBo.getId());
			reasonBo.setStatus(Constant.ADD_AGREE);
			reasonService.insert(reasonBo);
		} else {
			reasonService.updateApply(reasonBo.getId(), Constant.ADD_AGREE, "");
		}
		users.add(userBo.getId());
		circleAddUsers(circleid, users);
		userAddHis(userBo.getId(), circleBo.getId(), 1);
		return Constant.COM_RESP;
	}

	@RequestMapping("/my-info")
	@ResponseBody
	public String myInfo(HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		List<CircleBo> circleBoList = circleService.selectByuserid(userBo
				.getId());
		List<CircleVo> circleVoList = new LinkedList<CircleVo>();
		for (CircleBo circleBo : circleBoList) {
			CircleVo circleVo = new CircleVo();
			BeanUtils.copyProperties(circleBo, circleVo);
			circleVo.setUserAdd(1);
			circleVo.setNotesSize(circleBo.getNoteSize());
			circleVoList.add(circleVo);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("circleVoList", circleVoList);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/user-apply")
	@ResponseBody
	public String userApply(@RequestParam(required = true) String circleid,
			HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		if (!circleBo.getCreateuid().equals(userBo.getId()) &&
				circleBo.getMasters().contains(userBo.getId())) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_NOT_MASTER.getIndex(),
					ERRORCODE.CIRCLE_NOT_MASTER.getReason());
		}
		
		HashSet<String> usersApply = circleBo.getUsersApply();
		List<UserApplyVo> userApplyVos = new ArrayList<>();

		HashSet<String> users = circleBo.getUsers();

		boolean isChange = false;
		HashSet<String> removeUsers = new LinkedHashSet<>();
		for (String userid : usersApply) {
			if (users.contains(userid)) {
				removeUsers.add(userid);
				isChange = true;
				continue;
			}
			UserBo apply = userService.getUser(userid);
			if (apply != null) {
				UserApplyVo  userApplyVo = new UserApplyVo();
				userApplyVo.setBirthDay(apply.getBirthDay());
				userApplyVo.setHeadPictureName(apply.getHeadPictureName());
				userApplyVo.setCircleid(circleid);
				userApplyVo.setUserid(apply.getId());
				userApplyVo.setUserName(apply.getUserName());
				userApplyVo.setSex(apply.getSex());
				ReasonBo reasonBo = reasonService.findByUserAndCircle(apply.getId(), circleid, Constant.ADD_APPLY);
				if (reasonBo != null) {
					userApplyVo.setReason(reasonBo.getReason());
					userApplyVo.setStatus(reasonBo.getStatus());
					userApplyVo.setApplyTime(reasonBo.getCreateTime());
				}
				userApplyVos.add(userApplyVo);
			} else {
				removeUsers.add(userid);
				isChange = true;
			}
		}
		if (isChange) {
			usersApply.removeAll(removeUsers);
			circleAddUserApply(circleid, usersApply);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("usersApply", userApplyVos);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/user-apply-agree")
	@ResponseBody
	public String userApplyAgree(
			@RequestParam(required = true) String circleid,
			@RequestParam(required = true) String userids,
			HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		updateHistory(userBo.getId(), circleid, locationService, circleService);
		String[] useridArr = CommonUtil.getIds(userids);
		HashSet<String> users = circleBo.getUsers();
		if (users.size() >= 500 || (users.size() + useridArr.length) > 500) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_USER_MAX.getIndex(),
					ERRORCODE.CIRCLE_USER_MAX.getReason());
		}
		if (!circleBo.getCreateuid().equals(userBo.getId()) &&
				!circleBo.getMasters().contains(userBo.getId())) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_NOT_MASTER.getIndex(),
					ERRORCODE.CIRCLE_NOT_MASTER.getReason());
		}
		HashSet<String> usersApply = circleBo.getUsersApply();
		List<String> accepts = new ArrayList<>();
		List<String> pushFriends = new ArrayList<>();
		String content = String.format("您加入圈子【%s】的申请已通过，快去看看吧", circleBo.getName());
		for (String userid : useridArr) {
			UserBo user = userService.getUser(userid);
			if (null == user) {
				continue;
			}
			userAddHis(userid, circleid, 1);
			if (usersApply.contains(userid)) {
				usersApply.remove(userid);
				users.add(userid);
				ReasonBo reasonBo = reasonService.findByUserAdd(userid, circleid);
				if (reasonBo != null) {
					if (reasonBo.getStatus() != Constant.ADD_AGREE) {
						reasonService.updateApply(reasonBo.getId(), Constant.ADD_AGREE, "");
					}
					if (reasonBo.isNotice()) {
						pushFriends.add(userid);
					}
					//是否通过聚会页面加入圈子
					if (reasonBo.getAddType() == 1) {
						String party = String.format("/party/party-info.do?partyid=%s", reasonBo.getPartyid());
						JPushUtil.push(titlePush, content, party,  userid);
					} else {
						accepts.add(userid);
					}
				} else {
					reasonBo = new ReasonBo();
					reasonBo.setCircleid(circleid);
					reasonBo.setCreateuid(userid);
					reasonBo.setStatus(Constant.ADD_AGREE);
					reasonService.insert(reasonBo);
					accepts.add(userid);
				}
			}
		}
		circleAddUsers(circleBo.getId(), users, usersApply);
		String path = "/circle/circle-info.do?circleid=" + circleid;
		if (!accepts.isEmpty()) {
			String[] userArr = new String[accepts.size()];
			accepts.toArray(userArr);
			JPushUtil.push(titlePush, content, path,  userArr);
		}
		pushToFriends(circleBo.getName(), path, pushFriends);
		return Constant.COM_RESP;
	}

	/**
	 * 推送给好友
	 * @param path
	 * @param accepts
	 */
	@Async
	private void pushToFriends(String circleName, String path, List<String> accepts){
		for (String userid : accepts) {
			UserBo userBo = userService.getUser(userid);
			if (userBo == null) {
				continue;
			}
			List<FriendsBo> friendsBos = friendsService.getFriendByUserid(userid);
			for (FriendsBo friendsBo : friendsBos) {
				UserBo friend = userService.getUser(friendsBo.getFriendid());
				if (friend == null) {
					continue;
				}
				String name = "";
				FriendsBo bo = friendsService.getFriendByIdAndVisitorIdAgree(friendsBo.getFriendid(), userid);
				if (bo != null) {
					name = bo.getBackname();
				}
				if  (StringUtils.isEmpty(name)) {
					name = userBo.getUserName();
				}
				String content = String.format("“%s”已申请加入圈子【%s】，你也快去看看吧",name, circleName);
				JPushUtil.push(titlePush, content, path, friend.getId());
			}
		}
	}

	/**
	 * 用户加入圈子记录
	 * @param userid
	 * @param circleid
	 * @param status
	 */
	@Async
	private void userAddHis(String userid, String circleid, int status){
		CircleAddBo addBo = circleService.findHisByUserAndCircle(userid, circleid);
		if (addBo == null) {
			addBo = new CircleAddBo();
			addBo.setUserid(userid);
			addBo.setCircleid(circleid);
			addBo.setStatus(status);
			circleService.insertCircleAdd(addBo);
		} else if (addBo.getStatus() != status) {
			circleService.updateJoinStatus(addBo.getId(), status);
		}
	}

	@RequestMapping("/user-apply-refuse")
	@ResponseBody
	public String userApplyRefuse(
			@RequestParam(required = true) String circleid,
			@RequestParam(required = true) String userids,
			String refuse, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		updateHistory(userBo.getId(), circleid, locationService, circleService);
		if (!circleBo.getCreateuid().equals(userBo.getId()) &&
				!circleBo.getMasters().contains(userBo.getId())) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_NOT_MASTER.getIndex(),
					ERRORCODE.CIRCLE_NOT_MASTER.getReason());
		}
		String[] useridArr = CommonUtil.getIds(userids);
		HashSet<String> usersApply = circleBo.getUsersApply();
		HashSet<String> usersRefuse = circleBo.getUsersRefuse();
		for (String userid : useridArr) {
			if (StringUtils.isNotEmpty(userid)) {
				UserBo user = userService.getUser(userid);
				if (null == user) {
					continue;
				}
				if (!usersApply.contains(userid)) {
					return CommonUtil.toErrorResult(
							ERRORCODE.CIRCLE_APPLY_USER_NULL.getIndex(),
							ERRORCODE.CIRCLE_APPLY_USER_NULL.getReason());
				}
				usersApply.remove(userid);
				usersRefuse.add(userid);
				userAddHis(userid, circleid, 0);
				ReasonBo reasonBo = reasonService.findByUserAndCircle(userid, circleid, Constant.ADD_APPLY);
				if (reasonBo != null) {
					reasonService.updateApply(reasonBo.getId(), Constant.ADD_REFUSE, refuse);
				}
			}
		}
		circleAddUsersRefuse(circleBo.getId(),usersApply, usersRefuse);
		return Constant.COM_RESP;
	}

	@RequestMapping("/list")
	@ResponseBody
	public String list(@RequestParam(required = true) String tag,
			@RequestParam(required = true) String sub_tag,
			@RequestParam(required = true) String category,
			HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		List<CircleBo> list = circleService
				.selectByType(tag, sub_tag, category);
		return bo2vos(list, userBo);
	}


	@RequestMapping("/delete-user")
	@ResponseBody
	public String delete(@RequestParam(required = true) String circleid,
						 @RequestParam(required = true) String userids,
						 HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		updateHistory(userBo.getId(), circleid, locationService, circleService);
		String[] useridArr = CommonUtil.getIds(userids);
		//圈主才能删除管理员
		LinkedHashSet<String> masters = circleBo.getMasters();
		HashSet<String> users = circleBo.getUsers();
		HashSet<String> removes = new LinkedHashSet<>();
		int removeUser = 0;
		for (String userid : useridArr) {
			if (masters.contains(userid)) {
				if (!circleBo.getCreateuid().equals(userBo.getId())) {
					return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_NOT_MASTER.getIndex(),
							ERRORCODE.CIRCLE_NOT_MASTER.getReason());
				}
				masters.remove(userid);
				circleService.updateMaster(circleBo);
			} else {
				if (!circleBo.getCreateuid().equals(userBo.getId()) &&
						!circleBo.getMasters().contains(userBo.getId())) {
					return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_NOT_MASTER.getIndex(),
							ERRORCODE.CIRCLE_NOT_MASTER.getReason());
				}
			}
			UserBo user = userService.getUser(userid);
			if (user != null) {
				List<String> circles = user.getCircleTops();
				if (circles.contains(circleid)) {
					circles.remove(circleid);
					userService.updateTopCircles(userid, circles);
					userAddHis(userid, circleid, 2);
				}
			}
			if (users.contains(userid)) {
				users.remove(userid);
				removes.add(userid);
				removeUser ++;
				userAddHis(userid, circleid, 2);
			}
		}
		if (removeUser > 0) {
			circleAddUsers(circleid, users);
			this.removeUsers(removes, circleid);
		}
		return Constant.COM_RESP;
	}

	@Async
	private void removeUsers(HashSet<String> userids, String circleid){
		reasonService.removeUser(userids, circleid);
	}

	@RequestMapping("/transfer")
	@ResponseBody
	public String transfer(@RequestParam(required = true) String circleid,
						   @RequestParam(required = true) String userid,
						   HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		updateHistory(userBo.getId(), circleid, locationService, circleService);
		if (!circleBo.getCreateuid().equals(userBo.getId())) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_NOT_MASTER.getIndex(),
					ERRORCODE.CIRCLE_NOT_MASTER.getReason());
		}
		if (userBo.getId().equals(userid)) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_IS_SELF.getIndex(),
					ERRORCODE.CIRCLE_IS_SELF.getReason());
		}
		if (!circleBo.getUsers().contains(userid)) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_USER_NULL.getIndex(),
					ERRORCODE.CIRCLE_USER_NULL.getReason());
		}
		//创建者默认为群主
		circleBo.setCreateuid(userid);
		circleBo.setUpdateuid(userBo.getId());
		circleService.updateCreateUser(circleBo);
		LinkedHashSet<String> masters = circleBo.getMasters();
		if (masters.contains(userid)) {
			masters.remove(userid);
			circleService.updateMaster(circleBo);
		}
		FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorIdAgree(userid, userBo.getId());
		String name = userBo.getUserName();
		if (friendsBo != null) {
			 name = StringUtils.isEmpty(friendsBo.getBackname()) ? name : friendsBo.getBackname();
		}
		String content = String.format("“%s”将您设置为圈主", name);
		String path = "/circle/persons.do?circleid=" + circleid;
		JPushUtil.pushNotify("圈主转让通知", content, path,  userid);
		Map<String, String> params = new LinkedHashMap<>();
		params.put("circleName",circleBo.getName());
		params.put("circleid",circleid);
		params.put("operateTime",CommonUtil.getDateStr(new Date(),"yyyy-MM-dd HH:mm:ss"));
		params.put("operateUser",name);
		params.put("operateUserid",userBo.getId());
		JPushUtil.pushParams("圈主转让通知", content, path, params, userid);
		return Constant.COM_RESP;
	}

	/**
	 *  添加或删除管理员
	 * @param circleid
	 * @param userids
	 * @param isAdd  true是添加， false 是删除
	 * @return
	 */
	@RequestMapping("/master")
	@ResponseBody
	public String master(@RequestParam String circleid,
						 @RequestParam String userids,
						 @RequestParam boolean isAdd,
						   HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		updateHistory(userBo.getId(), circleid, locationService, circleService);
		if (!circleBo.getCreateuid().equals(userBo.getId())) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_NOT_MASTER.getIndex(),
					ERRORCODE.CIRCLE_NOT_MASTER.getReason());
		}
		String[] ids = CommonUtil.getIds(userids);
		LinkedHashSet<String> masters = circleBo.getMasters();
		HashSet<String> users = circleBo.getUsers();
		UserBo creater = userService.getUser(circleBo.getCreateuid());
		String content = "";
		String title = "";
		if (isAdd) {
			//判断圈子人数与管理员关系
			if (!hasMasterMax(users.size(), masters.size(), ids.length)) {
				return CommonUtil.toErrorResult(
						ERRORCODE.CIRCLE_MASTER_MAX.getIndex(),
						ERRORCODE.CIRCLE_MASTER_MAX.getReason());
			}
			for (String id : ids) {
				if (users.contains(id)) {
					masters.add(id);
				}
			}
			title = "设置管理员通知";
			content = String.format("“%s”将您设置为管理员",creater.getUserName());
		} else {
			for (String id : ids) {
				if (masters.contains(id)) {
					masters.remove(id);
				}
			}
			title = "取消管理员通知";
			content = String.format("“%s”已取消您的管理员",creater.getUserName());
		}
		circleService.updateMaster(circleBo);
		String path = "/circle/persons.do?circleid=" + circleid;
		Map<String, String> params = new LinkedHashMap<>();
		params.put("circleName",circleBo.getName());
		params.put("circleid",circleid);
		params.put("operateTime",CommonUtil.getDateStr(new Date(),"yyyy-MM-dd HH:mm:ss"));
		params.put("operateUser",userBo.getUserName());
		params.put("operateUserid",userBo.getId());
		JPushUtil.pushParams(title, content, path, params, ids);
		return Constant.COM_RESP;
	}

	/**
	 * 判断圈子管理员是否已到上线
	 * @param userNum
	 * @param masterNum
	 * @param addNum
	 * @return
	 */
	private boolean hasMasterMax(int userNum, int masterNum, int addNum){
		if (userNum<= 100  &&  masterNum + addNum > 5) {
			return false;
		}
		if (userNum <= 250  &&  masterNum + addNum > 10) {
			return false;
		}
		if (userNum <= 500  &&  masterNum + addNum > 15) {
			return false;
		}
		return true;
	}

	@RequestMapping("/quit")
	@ResponseBody
	public String delete(@RequestParam(required = true) String circleid,
						 HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		if (circleBo.getCreateuid().equals(userBo.getId())) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_NOT_QUIT.getIndex(),
					ERRORCODE.CIRCLE_NOT_QUIT.getReason());
		}
		LinkedHashSet<String> masters = circleBo.getMasters();
		if (masters.contains(userBo.getId())) {
			masters.remove(userBo.getId());
			circleBo.setMasters(masters);
			circleService.updateMaster(circleBo);
		}
		//删除置顶的圈子
		List<String> circles = userBo.getCircleTops();
		if (circles.contains(circleid)) {
			circles.remove(circleid);
			userService.updateTopCircles(userBo.getId(), circles);
		}
		HashSet<String> users = circleBo.getUsers();
		users.remove(userBo.getId());
		circleAddUsers(circleBo.getId(), users);
		userAddHis(userBo.getId(), circleid, 2);
		reasonService.removeUser(userBo.getId(), circleid);
		return Constant.COM_RESP;
	}

	@RequestMapping("/my-circles")
	@ResponseBody
	public String myCircles(String start_id, boolean gt, int limit, HttpServletRequest request,
						 HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		//置顶的圈子id
		List<String> topCircles = userBo.getCircleTops();
		List<CircleBo> circleBos = circleService.findMyCircles(userBo.getId(), start_id, gt, limit);
		//未置顶的圈子
		List<CircleBo> noTops = new LinkedList<>();
		List<CircleVo> voList = new LinkedList<>();
		String userid = userBo.getId();
		//筛选出置顶的圈子
		for (CircleBo circleBo : circleBos) {
			if (circleBo.getTotal() == 0) {
				int number = noteService.selectPeopleNum(circleBo.getId());
				circleBo.setTotal(number);
				circleService.updateTotal(circleBo.getId(), number);
			}
			if (topCircles.contains(circleBo.getId())) {
				voList.add(bo2vo(circleBo, userBo, 1));
			} else {
				noTops.add(circleBo);
			}
		}
		for (CircleBo item : noTops) {
			voList.add(bo2vo(item, userBo, 0));
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("circleVoList", voList);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 返回10个热门圈子（以圈子内人数排序，人数最多的10个圈子）
	 */
	@RequestMapping("/guess-you-like")
	@ResponseBody
	public String youLike(HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		LocationBo locationBo = locationService.getLocationBoByUserid(userBo.getId());
		List<CircleBo> circleBos = circleService.selectUsersLike(userBo.getId(), locationBo.getPosition(), 5000);
		return bo2vos(circleBos, userBo);
	}

	/**
	 * 圈子详情
	 */
	@RequestMapping("/circle-info")
	@ResponseBody
	public String info(String circleid, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		int userAdd = 0;
		if (userBo != null) {
			CircleAddBo addBo = circleService.findHisByUserAndCircle(userBo.getId(), circleid);
			userAdd = addBo != null ? addBo.getStatus() : 0;
			//更新访问记录
			updateHistory(userBo.getId(), circleid, locationService, circleService);
			updateCircieUnReadZero(userBo.getId(), circleid);
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		//圈子访问
		updateCircleHot(circleService, redisServer, circleid, 1, Constant.CIRCLE_VISIT);
		CircleVo circleVo = new CircleVo();
		BeanUtils.copyProperties(circleBo, circleVo);
		circleVo.setName(circleBo.getName());
		circleVo.setUsersSize(circleBo.getTotal());
		circleVo.setNotesSize(circleBo.getNoteSize());
		circleVo.setUserAdd(userAdd);
		LinkedHashSet<String> masters = circleBo.getMasters();
		//管理员
		List<UserBaseVo> mastersList = new ArrayList<>();
		for (String master : masters) {
			UserBo masterBo = userService.getUser(master);
			UserBaseVo userVo = new UserBaseVo();
			BeanUtils.copyProperties(masterBo, userVo);
			userVo.setRole(1);
			mastersList.add(userVo);
		}
		HashSet<String> users = circleBo.getUsers();
		List<UserBaseVo> userList = new ArrayList<>();
		for (String userId : users) {
			if (circleBo.getCreateuid().equals(userId) || masters.contains(userId)) {
				continue;
			}
			UserBo user = userService.getUser(userId);
			if (user != null) {
				UserBaseVo userBaseVo = new UserBaseVo();
				BeanUtils.copyProperties(user, userBaseVo);
				userBaseVo.setRole(0);
				userList.add(userBaseVo);
			}
		}
		//圈主
		UserBo hostBo = userService.getUser(circleBo.getCreateuid());
		UserBaseVo userHostVo = new UserBaseVo();
		BeanUtils.copyProperties(hostBo, userHostVo);
		userHostVo.setRole(2);

		long partyNum = partyService.getCirclePartyNum(circleid);

		//清零访问
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("ret", 0);
		map.put("creater", userHostVo);
		map.put("masters", mastersList);
		map.put("userVos", userList);
		map.put("circleVo", circleVo);
		map.put("partyNum", partyNum);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 红人列表，总榜及周榜
	 */
	@RequestMapping("/red-star-list")
	@ResponseBody
	public String redTopTotal(String circleid, HttpServletRequest request, HttpServletResponse response) {
		//如果登录就显示浏览记录

        UserBo userBo = getUserLogin(request);
        if (null != userBo){
            updateHistory(userBo.getId(), circleid, locationService, circleService);
        }

		Date currentDate = new Date();
		int weekNo = CommonUtil.getWeekOfYear(currentDate);
		int year = CommonUtil.getYear(currentDate);

		List<RedstarBo> total = userService.findRedUserTotal(circleid);

		List<RedstarBo> week = userService.findRedUserWeek(circleid, weekNo, year);

		List<UserStarVo>  totals = getStar(total);

		List<UserStarVo>  weeks = getStar(week);

		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("total", totals);
		map.put("week", weeks);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 置顶圈子
	 */
	@RequestMapping("/set-top")
	@ResponseBody
	public String setTopCircle(String circleid, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = null;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (null == circleBo) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		updateHistory(userBo.getId(), circleid, locationService, circleService);
		List<String> topList = userBo.getCircleTops();
		if (topList.contains(circleid)) {
			topList.remove(circleid);
		}
		topList.add(0,circleid);
		userService.updateTopCircles(userBo.getId(), topList);
		return Constant.COM_RESP;
	}
	/**
	 * 取消置顶圈子
	 */
	@RequestMapping("/cancel-top")
	@ResponseBody
	public String cancelTopCircle(String circleid, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (null == circleBo) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		updateHistory(userBo.getId(), circleid, locationService, circleService);
		List<String> topList = userBo.getCircleTops();
		if (topList.contains(circleid)) {
			topList.remove(circleid);
			userService.updateTopCircles(userBo.getId(), topList);
		}
		return Constant.COM_RESP;
	}


	/**
	 * 搜索圈子
	 */
	@RequestMapping("/search")
	@ResponseBody
	public String search(String keyword, HttpServletRequest request, HttpServletResponse response) {
		return  searchKeyword(keyword, 1, 10, request, response);
	}

	/**
	 * 搜索圈子
	 */
	@RequestMapping("/search-keyword")
	@ResponseBody
	public String searchKeyword(String keyword,int page, int limit,
								HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (StringUtils.isNotEmpty(keyword)) {
			List<CircleBo> circleBos = circleService.findBykeyword(keyword, page, limit);
			saveKeyword(keyword);
			return bo2vos(circleBos, userBo);
		}
		return Constant.COM_RESP;
	}

	@Async
	private void saveKeyword(String keyword){
		CircleTypeBo typeBo = circleService.findByName(keyword, 2);
		if (typeBo != null){
			RLock lock = redisServer.getRLock("keyword");
			try {
				lock.lock(2, TimeUnit.SECONDS);
				SearchBo searchBo = searchService.findByKeyword(keyword, 0);
				if (searchBo == null) {
					searchBo = new SearchBo();
					searchBo.setKeyword(keyword);
					searchBo.setType(0);
					searchBo.setTimes(1);
					searchService.insert(searchBo);
				} else {
					searchService.update(searchBo.getId());
				}
			} finally {
				lock.unlock();
			}
		}
	}

	/**
	 * 根据类型获取圈子
	 */
	@RequestMapping("/get-by-type")
	@ResponseBody
	public String getByType(String tag, String sub_tag , int page, int limit,
							HttpServletRequest request, HttpServletResponse response) {
		List<CircleBo> circleBos = circleService.findByType(tag, sub_tag, page, limit);
		if (StringUtils.isNotEmpty(sub_tag)) {
			saveKeyword(sub_tag);
		}
		if (StringUtils.isNotEmpty(tag)) {
			saveKeyword(tag);
		}
		HttpSession session = request.getSession();
		boolean isLogin;
		UserBo userBo = null;
		if (session.isNew() || session.getAttribute("isLogin") == null) {
			isLogin = false;
		} else {
			userBo = (UserBo) session.getAttribute("userBo");
			isLogin = userBo != null;
		}
		List<CircleVo> listVo = new LinkedList<>();
		for (CircleBo circleBo: circleBos) {
			CircleVo circleVo = new CircleVo();
			BeanUtils.copyProperties(circleBo, circleVo);
			circleVo.setId(circleBo.getId());
			circleVo.setName(circleBo.getName());
			circleVo.setNotesSize(circleBo.getNoteSize());
			circleVo.setUsersSize(circleBo.getTotal());
			if (isLogin) {
				CircleAddBo addBo = circleService.findHisByUserAndCircle(userBo.getId(), circleBo.getId());
				if (null != addBo) {
					circleVo.setUserAdd(addBo.getStatus());
				}
			}
			listVo.add(circleVo);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("circleVoList", listVo);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 *
	 */
	@RequestMapping("/get-creater")
	@ResponseBody
	public String getCreater(String circleid, HttpServletRequest request, HttpServletResponse response) {

		CircleBo circleBo = circleService.selectById(circleid);
		if (null == circleBo) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		//圈主
		UserBo hostBo = userService.getUser(circleBo.getCreateuid());
		UserBaseVo userHostVo = new UserBaseVo();
		BeanUtils.copyProperties(hostBo, userHostVo);
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("creater", userHostVo);
		return JSONObject.fromObject(map).toString();
	}
	/**
	 *
	 */
	@RequestMapping("/get-master")
	@ResponseBody
	public String getMaster(String circleid, HttpServletRequest request, HttpServletResponse response) {
		CircleBo circleBo = circleService.selectById(circleid);
		if (null == circleBo) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		LinkedHashSet<String> masters = circleBo.getMasters();
		//管理员
		List<UserBaseVo> mastersList = new ArrayList<>();
		for (String master : masters) {
			UserBo masterBo = userService.getUser(master);
			UserBaseVo userVo = new UserBaseVo();
			BeanUtils.copyProperties(masterBo, userVo);
			mastersList.add(userVo);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("masters", mastersList);
		return JSONObject.fromObject(map).toString();
	}


	/**
	 * 获取圈子分类
	 */
	@RequestMapping("/circle-type")
	@ResponseBody
	public String circleType(HttpServletRequest request, HttpServletResponse response) {

		List<CircleTypeBo> typeBos = circleService.selectByLevel(1);
		Map<String, List<String>> maps = new LinkedHashMap<>();
		for (CircleTypeBo typeBo : typeBos) {
			List<CircleTypeBo> bos = circleService.selectByParent(typeBo.getCategory());
			List<String> boList = new ArrayList<>();
			for (CircleTypeBo bo : bos) {
				boList.add(bo.getCategory());
			}
			maps.put(typeBo.getCategory(), boList);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("types", maps);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 *添加圈子分类
	 */
	@RequestMapping("/add-circle-type")
	@ResponseBody
	public String addCircleType(String name, String parent, int level, HttpServletRequest request,
								HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CircleTypeBo typeBo = circleService.findByName(name, level);
		if (typeBo == null) {
			typeBo = new CircleTypeBo();
			typeBo.setCategory(name);
			typeBo.setLevel(level);
			if (StringUtils.isNotEmpty(parent)) {
				typeBo.setPreCateg(parent);
			}
			typeBo.setType(0);
			typeBo.setCreateuid(userBo.getId());
		} else {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_TYPE_EXIST.getIndex(),
					ERRORCODE.CIRCLE_TYPE_EXIST.getReason());
		}
		return Constant.COM_RESP;
	}

	/**
	 * 更多圈子时，获取所有分类
	 */
	@RequestMapping("/circle-type-search")
	@ResponseBody
	public String circleTypeSearch(HttpServletRequest request, HttpServletResponse response) {
		List<CircleTypeBo> typeBos = circleService.findAllCircleTypes();
		List<String> types = new ArrayList<>();
		for (CircleTypeBo typeBo : typeBos) {
			types.add(typeBo.getCategory());
		}
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("types", types);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 附近活跃人员
	 */
	@RequestMapping("/near-people")
	@ResponseBody
	public String nearPeopel(String circleid, double px, double py, HttpServletRequest request, HttpServletResponse
			response) {
		String userid = "";
		try {
			UserBo userBo = checkSession(request, userService);
			userid = userBo.getId();
		} catch (MyException e) {
			userid = "";
		}
		double[] position = new double[]{px, py};
		List<CircleHistoryBo> historyBos = circleService.findNearPeople(circleid,
				userid,position, 20000);
		List<UserBaseVo> userVos = new ArrayList<>();
		for (CircleHistoryBo historyBo : historyBos) {
			UserBo user = userService.getUser(historyBo.getUserid());
			UserBaseVo baseVo = new UserBaseVo();
			BeanUtils.copyProperties(user, baseVo);
			userVos.add(baseVo);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("userVoList", userVos);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 附近圈子
	 */
	@RequestMapping("/near-circle")
	@ResponseBody
	public String nearPeopel(double px, double py, HttpServletRequest request, HttpServletResponse
			response) {
		HttpSession session = request.getSession();
		double[] position = new double[]{px, py};
		//未登录情况
		String userid = "";
		if (!session.isNew() &&  session.getAttribute("isLogin") != null) {
			UserBo userBo = (UserBo) session.getAttribute("userBo");
			if (userBo != null) {
				userid = userBo.getId();
			}
		}
		UserBo userBo = getUserLogin(request);
		List<CircleBo> circleBos = circleService.findNearCircle(userid, position, 10000, 10);
		return bo2vos(circleBos, userBo);
	}


	/**
	 * 判断当前用户在圈子中的身份
	 */
	@RequestMapping("/circle-role")
	@ResponseBody
	public String ciecleRle(String circleid, HttpServletRequest request, HttpServletResponse
			response) {
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		//判断当前用户是否已经登录
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (Exception e) {
			map.put("role", 0);
			return JSONObject.fromObject(map).toString();
		}
		if (circleBo.getCreateuid().equals(userBo.getId())) {
			map.put("role", 2);
		} else if (circleBo.getMasters().contains(userBo.getId())) {
			map.put("role", 1);
		} else {
			map.put("role", 0);
		}
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 圈子中的用户
	 */
	@RequestMapping("/persons")
	@ResponseBody
	public String cieclePerson(String circleid, HttpServletRequest request, HttpServletResponse
			response) {
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		UserBo userBo = getUserLogin(request);
		String userid = userBo != null ? userBo.getId() : "";
		HashSet<String> users = circleBo.getUsers();
		HashSet<String> masters = circleBo.getMasters();
		List<UserCircleVo> userList = new ArrayList<>();
		for (String userId : users) {
			UserBo user = userService.getUser(userId);
			if (user == null) {
				continue;
			}
			long circleNums = circleService.findCreateCricles(userId);
			UserCircleVo userCircleVo = new UserCircleVo();
			BeanUtils.copyProperties(user, userCircleVo);
			if (!"".equals(userid)) {
				FriendsBo bo = friendsService.getFriendByIdAndVisitorIdAgree(userid, user.getId());
				if (bo != null) {
					userCircleVo.setBackName(bo.getBackname());
				}
			}
			if (circleBo.getCreateuid().equals(userId)) {
				userCircleVo.setRole(2);
			} else if (masters.contains(userId)){
				userCircleVo.setRole(1);
			} else {
				userCircleVo.setRole(0);
			}
			userCircleVo.setMaxCircleNum(userCircleVo.getLevel() * 5);
			userCircleVo.setHasCircleNum((int)circleNums);
			userList.add(userCircleVo);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("userVoList", userList);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 修改圈子名称
	 */
	@RequestMapping("/update-name")
	@ResponseBody
	public String updateName(@RequestParam String circleid, @RequestParam String name,
							   HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		if(circleBo.getMasters().contains(userBo.getId()) || circleBo.getCreateuid().equals(userBo.getId())) {
			if (StringUtils.isNotEmpty(name)){
				CircleBo circle = circleService.findByTagAndName(name, circleBo.getTag(), circleBo.getSub_tag());
				if (circle != null) {
					return CommonUtil.toErrorResult(
							ERRORCODE.CIRCLE_NAME_EXIST.getIndex(),
							ERRORCODE.CIRCLE_NAME_EXIST.getReason());
				}
				circleService.updateCircleName(circleid, name);
			} else {
				return Constant.COM_FAIL_RESP;
			}
		} else {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_NOT_MASTER.getIndex(),
					ERRORCODE.CIRCLE_NOT_MASTER.getReason());
		}
		return Constant.COM_RESP;
	}


    /**
     * 是否允许加入
     */
    @RequestMapping("/open")
    @ResponseBody
    public String updateOpen(@RequestParam String circleid, boolean open,
                             HttpServletRequest request, HttpServletResponse response) {
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        CircleBo circleBo = circleService.selectById(circleid);
        if (circleBo == null) {
            return CommonUtil.toErrorResult(
                    ERRORCODE.CIRCLE_IS_NULL.getIndex(),
                    ERRORCODE.CIRCLE_IS_NULL.getReason());
        }
        if (circleBo.getCreateuid().equals(userBo.getId()) ||
                circleBo.getMasters().contains(userBo.getId())) {
            circleService.updateOpen(circleid, open);
        } else {
            return CommonUtil.toErrorResult(
                    ERRORCODE.CIRCLE_MASTER_NULL.getIndex(),
                    ERRORCODE.CIRCLE_MASTER_NULL.getReason());
        }
        return Constant.COM_RESP;
    }

    /**
     * 是否允许加入
     */
    @RequestMapping("/verify")
    @ResponseBody
    public String updateVerify(@RequestParam String circleid, boolean verify,
                             HttpServletRequest request, HttpServletResponse response) {
        UserBo userBo;
        try {
            userBo = checkSession(request, userService);
        } catch (MyException e) {
            return e.getMessage();
        }
        CircleBo circleBo = circleService.selectById(circleid);
        if (circleBo == null) {
            return CommonUtil.toErrorResult(
                    ERRORCODE.CIRCLE_IS_NULL.getIndex(),
                    ERRORCODE.CIRCLE_IS_NULL.getReason());
        }
        if (circleBo.getCreateuid().equals(userBo.getId()) ||
                circleBo.getMasters().contains(userBo.getId())) {
            circleService.updateisVerify(circleid, verify);
        } else {
            return CommonUtil.toErrorResult(
                    ERRORCODE.CIRCLE_MASTER_NULL.getIndex(),
                    ERRORCODE.CIRCLE_MASTER_NULL.getReason());
        }
        return Constant.COM_RESP;
    }

	/**
	 * 添加或修改公告
	 */
	@RequestMapping("/notice")
	@ResponseBody
	public String ciecleNotice(@RequestParam String circleid,
							   String title, String content,
							   HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		String userid = userBo.getId();
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		if (circleBo.getCreateuid().equals(userid) ||
				circleBo.getMasters().contains(userid)) {
			circleBo.setNotice(content);
			circleBo.setNoticeTitle(title);
			circleBo.setNoticeTime(new Date());
			circleBo.setNoticeUserid(userid);
			circleService.updateNotice(circleBo);
		} else {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_MASTER_NULL.getIndex(),
					ERRORCODE.CIRCLE_MASTER_NULL.getReason());
		}
		return Constant.COM_RESP;
	}

	/**
	 * 添加或修改公告
	 */
	@RequestMapping("/get-notice")
	@ResponseBody
	public String getNotice(@RequestParam String circleid,
							HttpServletRequest request, HttpServletResponse response) {
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}

		UserBo userBo = userService.getUser(circleBo.getNoticeUserid());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("noticeTitle", circleBo.getNoticeTitle());
		map.put("notice", circleBo.getNotice());
		map.put("noticeTime", circleBo.getNoticeTime());
		if (userBo != null) {
			UserBaseVo userBaseVo = new UserBaseVo();
			BeanUtils.copyProperties(userBo, userBaseVo);
			map.put("noticeUser", userBaseVo);
		}
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 举报圈子
	 */
	@RequestMapping("/feed-tips")
	@ResponseBody
	public String feedTips(@RequestParam String circleid, @RequestParam String title,
						   @RequestParam String content ,@RequestParam(required = false) String contact,
						   @RequestParam(required = false) MultipartFile[] images,
						   HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		FeedbackBo feedbackBo = new FeedbackBo();
		feedbackBo.setOwnerId(userBo.getId());
		feedbackBo.setCreateuid(userBo.getId());
		feedbackBo.setTargetId(circleid);
		feedbackBo.setType(Constant.FEED_TIPS);
		feedbackBo.setSubType(Constant.CIRCLE_TYPE);
		feedbackBo.setTargetTitle(title);
		feedbackBo.setContent(content);
		feedbackBo.setContactInfo(contact);
		if (images != null) {
			LinkedList<String> imagesList = new LinkedList<>();
			Long time = Calendar.getInstance().getTimeInMillis();
			for (MultipartFile file : images) {
				String fileName = userBo.getId() + "-" + time + "-"
						+ file.getOriginalFilename();
				logger.info(fileName);
				String path = CommonUtil.upload(file, Constant.FEEDBACK_PICTURE_PATH,
						fileName, 0);
				imagesList.add(path);
			}
			feedbackBo.setImages(imagesList);
		}
		feedbackService.insert(feedbackBo);
		return Constant.COM_RESP;
	}


	/**
	 * 获取热门搜索关键词
	 */
	@RequestMapping("/hot-searchs")
	@ResponseBody
	public String hotSearchs(HttpServletRequest request, HttpServletResponse response) {

		List<SearchBo> searchBos = searchService.findByTimes(0, 12);

		List<String> words = new ArrayList<>();
		for (SearchBo searchBo : searchBos) {
			words.add(searchBo.getKeyword());
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("hotWords", words);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 根据城市位置搜索圈子
	 */
	@RequestMapping("/search-city")
	@ResponseBody
	public String searchCitys(String province, String city, String district , int page, int limit,
							  HttpServletRequest request, HttpServletResponse response) {

		if (StringUtils.isNotEmpty(city)){
			seveKeys(city);
		}
		if (StringUtils.isNotEmpty(district)){
			seveKeys(district);
		}
		List<CircleBo> circleBos = circleService.findByCitys(province, city, district, page, limit);
		UserBo userBo = getUserLogin(request);
		return bo2vos(circleBos, userBo);
	}

	private void seveKeys(String value){
		SearchBo searchBo = searchService.findByKeyword(value, 0);
		if (searchBo == null) {
			searchBo = new SearchBo();
			searchBo.setKeyword(value);
			searchBo.setType(4);
			searchBo.setTimes(1);
			searchService.insert(searchBo);
		} else {
			searchService.update(searchBo.getId());
		}

	}

	/**
	 * 热门城市
	 */
	@RequestMapping("/hot-citys")
	@ResponseBody
	public String hotCitys(HttpServletRequest request, HttpServletResponse response) {

		List<SearchBo> searchBos = searchService.findByTimes(4, 9);

		List<String> words = new ArrayList<>();
		for (SearchBo searchBo : searchBos) {
			words.add(searchBo.getKeyword());
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("hotCitys", words);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 相关圈子
	 */
	@RequestMapping("/related")
	@ResponseBody
	public String relatedCircle(String circleid, int page, int limit, HttpServletRequest request, HttpServletResponse
			response) {

		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		List<CircleBo> circleBos = circleService.findRelatedCircles(
				circleid, circleBo.getTag(), circleBo.getSub_tag(), page, limit);
		if (circleBos == null || circleBos.isEmpty()) {
			circleBos = circleService.findRelatedCircles(
					circleid, circleBo.getTag(), "", page, limit);
		}
		return bo2vos(circleBos,getUserLogin(request));
	}

	/**
	 * 邀请加入圈子
	 * @param circleid
	 * @param userids
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/invite-user")
	@ResponseBody
	public String inviteUsers(@RequestParam String circleid, @RequestParam String userids,
						   HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		String userid = userBo.getId();
		String[] useridArr = CommonUtil.getIds(userids);
		if ((circleBo.getUsers().size() + useridArr.length) > 500) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_USER_MAX.getIndex(),
					ERRORCODE.CIRCLE_USER_MAX.getReason());
		}
		String path = "/circle/circle-info.do?circleid=" + circleid;
		String content = String.format("“%s”邀请您加入圈子【%s】，快去看看吧", userBo.getUserName(),
				circleBo.getName());
		updateHistory(userid, circleid, locationService, circleService);
		if (circleBo.getCreateuid().equals(userid) ||
				circleBo.getMasters().contains(userid)) {
			for (String inviteId : useridArr) {
				ReasonBo reasonBo = reasonService.findByUserAndCircle(inviteId, circleid, Constant.ADD_APPLY);
				if (reasonBo == null) {
					reasonBo = new ReasonBo();
					reasonBo.setCircleid(circleid);
					reasonBo.setCreateuid(inviteId);
					reasonBo.setMasterApply(true);
					reasonBo.setStatus(Constant.ADD_APPLY);
					reasonService.insert(reasonBo);
				} else {
					reasonService.updateMasterApply(reasonBo.getId(), Constant.ADD_APPLY, true);
				}
			}
			JPushUtil.push(titlePush, content, path, useridArr);
		} else {
			JPushUtil.push(titlePush, content, path,  useridArr);
		}
		return Constant.COM_RESP;
	}


	/**
	 * 邀请好友列表
	 * @param circleid
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/invite-friend-list")
	@ResponseBody
	public String inviteList(@RequestParam String circleid,
							  HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		List<FriendsBo> friendsBos = friendsService.getFriendByUserid(userBo.getId());
		HashSet<String> circleUsers = circleBo.getUsers();
		List<FriendsVo> voList = new ArrayList<>();
		for (FriendsBo friendsBo : friendsBos) {
			FriendsVo vo = new FriendsVo();
			BeanUtils.copyProperties(friendsBo, vo);
			String friendid = friendsBo.getFriendid();
			if (circleUsers.contains(friendid)) {
				continue;
			}
			UserBo friend = userService.getUser(friendsBo.getFriendid());
			if (friend == null) {
				friendsService.delete(userBo.getId(), friendid);
				continue;
			}
			vo.setUsername(friend.getUserName());
			vo.setPicture(friend.getHeadPictureName());
			vo.setBackname(friendsBo.getBackname());
			vo.setUserid("");
			voList.add(vo);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("userVos", voList);
		return JSONObject.fromObject(map).toString();
	}



	/**
	 * 圈子好友搜索
	 * @param circleid
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/user-search")
	@ResponseBody
	public String userSearch(String circleid, String keyword,
							 HttpServletRequest request, HttpServletResponse response) {

		UserBo userBo = getUserLogin(request);

		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}

		List<String> userHas = new ArrayList<>();
		List<UserBo> userBos = userService.searchCircleUsers(circleBo.getUsers(), keyword);
		HashSet<String> masters = circleBo.getMasters();
		List<UserBaseVo> userList = new ArrayList<>();
		for (UserBo user : userBos) {
			String userid =user.getId();
			if (userBo != null && userBo.getId().equals(userid)) {
				continue;
			}
			UserCircleVo userBaseVo = circleUser2Vo(user, circleBo.getCreateuid(), masters);
			long circleNum = circleService.findCreateCricles(userid);

			userBaseVo.setHasCircleNum((int)circleNum);
			userBaseVo.setMaxCircleNum(user.getLevel() * 5);
			userList.add(userBaseVo);
			userHas.add(userid);
		}
		//登录后可以查询好友信息的
		if (userBo != null){
			List<FriendsBo> friendsBos = friendsService.searchCircleUsers(circleBo.getUsers(),userBo.getId(),
					keyword);
			for (FriendsBo friend : friendsBos) {
				String friendid = friend.getFriendid();
				if (userHas.contains(friendid)) {
					continue;
				}
				UserBo user = userService.getUser(friendid);
				if (user == null){
					continue;
				}
				UserCircleVo userBaseVo = circleUser2Vo(user, circleBo.getCreateuid(), masters);
				long circleNum = circleService.findCreateCricles(friendid);
				userBaseVo.setHasCircleNum((int)circleNum);
				userBaseVo.setMaxCircleNum(user.getLevel() * 5);
				userList.add(userBaseVo);
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("userVoList", userList);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 邀请好友搜索
	 * @param circleid
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/invite-user-search")
	@ResponseBody
	public String inviteUserSearch(String circleid, String keyword,
							 HttpServletRequest request, HttpServletResponse response) {

		UserBo userBo = getUserLogin(request);
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		HashSet<String> masters = circleBo.getMasters();
		List<UserBaseVo> userList = new ArrayList<>();
		//登录后可以查询好友信息的
		if (userBo != null){
			List<FriendsBo> friendsBos = friendsService.searchInviteCircleUsers(circleBo.getUsers(),userBo.getId(),
					keyword);
			for (FriendsBo friend : friendsBos) {
				String friendid = friend.getFriendid();
				UserBo user = userService.getUser(friendid);
				if (user == null){
					continue;
				}
				UserCircleVo userBaseVo = circleUser2Vo(user, circleBo.getCreateuid(), masters);
				long circleNum = circleService.findCreateCricles(friendid);
				userBaseVo.setHasCircleNum((int)circleNum);
				userBaseVo.setMaxCircleNum(user.getLevel() * 5);
				userList.add(userBaseVo);
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("userVoList", userList);
		return JSONObject.fromObject(map).toString();
	}



	/**
	 * 加入圈子
	 * @param circleid
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/add-in-circle")
	@ResponseBody
	public String applyAddInsert(String circleid, HttpServletRequest request, HttpServletResponse
			response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		HashSet<String> users= circleBo.getUsers();
		if (users.contains(userBo.getId())) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_USER_EXIST.getIndex(),
					ERRORCODE.CIRCLE_USER_EXIST.getReason());
		}
		if (!circleBo.isOpen()) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_NEED_VERIFY.getIndex(),
					ERRORCODE.CIRCLE_NEED_VERIFY.getReason());
		}
		updateHistory(userBo.getId(), circleid, locationService, circleService);
		if (users.size() >= 500) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_USER_MAX.getIndex(),
					ERRORCODE.CIRCLE_USER_MAX.getReason());
		}
		boolean isAdd = false;
		if (!circleBo.isVerify()) {
			users.add(userBo.getId());
			circleAddUsers(circleid, users);
			addReason(userBo.getId(), circleid);
			isAdd = true;
		} else {
			ReasonBo reasonBo = reasonService.findByUserAdd(userBo.getId(), circleid);
			if (reasonBo != null) {
				if (reasonBo.getStatus() == Constant.ADD_APPLY) {
					isAdd = reasonBo.isMasterApply();
					if (isAdd) {
						users.add(userBo.getId());
						circleAddUsers(circleid, users);
						reasonService.updateApply(reasonBo.getId(), Constant.ADD_AGREE, "");
					}
				} else if (reasonBo.getStatus() == Constant.ADD_AGREE) {
					isAdd = true;
				}
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		if (isAdd) {
			userAddHis(userBo.getId(), circleBo.getId(), 1);
			map.put("ret", 0);
		} else {
			map.put("ret", -1);
		}
		return JSONObject.fromObject(map).toString();
	}


	/**
	 * 转发到我的动态
	 */
	@RequestMapping("/forward-dynamic")
	@ResponseBody
	public String forwardDynamic(String circleid, String view, HttpServletRequest request,
								 HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		DynamicBo dynamicBo = new DynamicBo();
		dynamicBo.setTitle(circleBo.getName());
		dynamicBo.setMsgid(circleid);
		dynamicBo.setOwner(circleBo.getCreateuid());
		LinkedHashSet<String> photos = new LinkedHashSet<>();
		photos.add(circleBo.getHeadPicture());
		dynamicBo.setPhotos(photos);
		dynamicBo.setCreateuid(userBo.getId());
		dynamicBo.setView(view);
		dynamicBo.setType(Constant.CIRCLE_TYPE);
		dynamicBo.setSourceName(circleBo.getName());
		dynamicService.addDynamic(dynamicBo);
		circleService.updateCircleHot(circleid, 1, Constant.CIRCLE_TRANS);
		updateDynamicNums(userBo.getId(), 1,dynamicService, redisServer);
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("dynamicid", dynamicBo.getId());
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 收藏帖子
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/collect-circle")
	@ResponseBody
	public String colNotes(String circleid, HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		CollectBo chatBo = collectService.findByUseridAndTargetid(userBo.getId(), circleid);
		if (chatBo != null) {
			return CommonUtil.toErrorResult(ERRORCODE.COLLECT_EXIST.getIndex(),
					ERRORCODE.COLLECT_EXIST.getReason());
		}
		chatBo = new CollectBo();
		chatBo.setCreateuid(userBo.getId());
		chatBo.setUserid(userBo.getId());
		chatBo.setTargetid(circleid);
		chatBo.setType(Constant.COLLET_URL);
		chatBo.setSub_type(Constant.CIRCLE_TYPE);
		chatBo.setTitle(circleBo.getName());
		chatBo.setTargetPic(circleBo.getHeadPicture());
		collectService.insert(chatBo);
		circleService.updateCircleHot(circleid,1, Constant.CIRCLE_TRANS);
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("col-time", CommonUtil.time2str(chatBo.getCreateTime()));
		return JSONObject.fromObject(map).toString();
	}


	private void addReason (String userid, String circleid){
		ReasonBo reasonBo = reasonService.findByUserAdd(userid, circleid);
		if (reasonBo == null) {
			reasonBo = new ReasonBo();
			reasonBo.setCircleid(circleid);
			reasonBo.setCreateuid(userid);
			reasonBo.setStatus(1);
			reasonBo.setAddType(0);
			reasonService.insert(reasonBo);
		} else if (reasonBo.getStatus() != 1) {
			reasonService.updateApply(reasonBo.getId(), 1, "");
		}
	}

	private UserCircleVo circleUser2Vo(UserBo userBo, String createuid, HashSet<String> masters){
		UserCircleVo userBaseVo = new UserCircleVo();
		BeanUtils.copyProperties(userBo, userBaseVo);
		if (createuid.equals(userBo.getId())) {
			userBaseVo.setRole(2);
		} else if (masters.contains(userBo.getId())){
			userBaseVo.setRole(1);
		} else {
			userBaseVo.setRole(0);
		}
		return userBaseVo;
	}


	/**
	 * 红人列表实体类转换
	 * @param redstarBos
	 * @return
	 */
	private List<UserStarVo> getStar(List<RedstarBo> redstarBos){
		List<UserStarVo>  userStarVos = new ArrayList<>();
		for (RedstarBo redstarBo : redstarBos) {
			UserBo userBo = userService.getUser(redstarBo.getUserid());
			if (userBo != null) {
				UserStarVo starVo = new UserStarVo();
				starVo.setId(userBo.getId());
				starVo.setHeadPictureName(userBo.getHeadPictureName());
				starVo.setUserName(userBo.getUserName());
				starVo.setTotalCount(redstarBo.getCommentTotal());
				starVo.setWeekCount(redstarBo.getCommentWeek());
				userStarVos.add(starVo);
			}
		}
		return  userStarVos;
	}
	/**
	 * 实体类转换
	 * @param circleBos
	 * @return
	 */
	private String bo2vos(List<CircleBo> circleBos, UserBo userBo){
		List<CircleVo> listVo = new LinkedList<CircleVo>();
		for (CircleBo item : circleBos) {
			listVo.add(bo2vo(item, userBo, 0));
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("circleVoList", listVo);
		return JSONObject.fromObject(map).toString();
	}

	private CircleVo bo2vo(CircleBo circleBo, UserBo userBo, int top){
		CircleVo circleVo = new CircleVo();
		BeanUtils.copyProperties(circleBo, circleVo);
		circleVo.setId(circleBo.getId());
		circleVo.setName(circleBo.getName());
		circleVo.setNotesSize(circleBo.getNoteSize());
		circleVo.setUsersSize(circleBo.getTotal());
		circleVo.setTop(top);
		if (null != userBo) {
			ReasonBo reasonBo = reasonService.findByUserAndCircle(userBo.getId(),
					circleBo.getId(), Constant.ADD_AGREE);
			//圈子未读数量信息
			if (reasonBo != null) {
				circleVo.setUnReadNum(reasonBo.getUnReadNum());
			}
			//查找圈子加入历史
			CircleAddBo addBo = circleService.findHisByUserAndCircle(userBo.getId(), circleBo.getId());
			if (null != addBo) {
				circleVo.setUserAdd(addBo.getStatus());
			}
		}
		return circleVo;
	}



	/**
	 * 清零数据
	 * @param userid
	 * @param circleid
	 */
	@Async
	private void updateCircieUnReadZero(String userid, String circleid){
		RLock lock = redisServer.getRLock(userid + "UnReadNumLock");
		try{
			lock.lock(2, TimeUnit.SECONDS);
			reasonService.updateUnReadNumZero(userid, circleid);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 加锁同意
	 * @param cirlceid
	 * @param type
	 * @param users
	 * @param userApplys
	 * @param refuses
	 */
	private void circleAddUsers(String cirlceid, int type, HashSet<String> users, HashSet<String> userApplys,
								HashSet<String> refuses){
		RLock lock = redisServer.getRLock(cirlceid + "add");
		try{
			lock.lock(3, TimeUnit.SECONDS);
			if (type == 0) {
				circleService.updateUsers(cirlceid, users);
			} else if (type == 1) {
				circleService.updateUsersApply(cirlceid, userApplys);
			}  else if (type == 2) {
				circleService.updateApplyAgree(cirlceid, users, userApplys);
			} else {
				circleService.updateUsersRefuse(cirlceid, userApplys, refuses);
			}
		} finally {
			lock.unlock();
		}

	}

	/**
	 * 加锁同意
	 * @param cirlceid
	 * @param users
	 */
	private void circleAddUsers(String cirlceid, HashSet<String> users){
		this.circleAddUsers(cirlceid, 0, users, null, null);
	}

	/**
	 * 加锁同意
	 * @param cirlceid
	 */
	private void circleAddUserApply(String cirlceid, HashSet<String> userApplys){
		this.circleAddUsers(cirlceid, 1, null, userApplys, null);
	}


	/**
	 * 加锁同意
	 * @param cirlceid
	 * @param users
	 */
	private void circleAddUsers(String cirlceid, HashSet<String> users, HashSet<String> userApplys){
		this.circleAddUsers(cirlceid, 2, users, userApplys, null);
	}

	/**
	 * 加锁同意
	 * @param cirlceid
	 */
	private void circleAddUsersRefuse(String cirlceid, HashSet<String> userApplys, HashSet<String> refuses){
		this.circleAddUsers(cirlceid, 3, null, userApplys, refuses);
	}
}
