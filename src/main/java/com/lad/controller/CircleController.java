package com.lad.controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonArray;
import com.lad.bo.CircleAddBo;
import com.lad.bo.CircleBo;
import com.lad.bo.CircleHistoryBo;
import com.lad.bo.CircleNoticeBo;
import com.lad.bo.CircleTypeBo;
import com.lad.bo.CollectBo;
import com.lad.bo.CommentBo;
import com.lad.bo.DynamicBo;
import com.lad.bo.FeedbackBo;
import com.lad.bo.FriendsBo;
import com.lad.bo.LocationBo;
import com.lad.bo.MessageBo;
import com.lad.bo.NoteBo;
import com.lad.bo.PartyBo;
import com.lad.bo.ReasonBo;
import com.lad.bo.RedstarBo;
import com.lad.bo.SearchBo;
import com.lad.bo.ShowBo;
import com.lad.bo.ThumbsupBo;
import com.lad.bo.UserBo;
import com.lad.redis.RedisServer;
import com.lad.scrapybo.BroadcastBo;
import com.lad.scrapybo.InforBo;
import com.lad.scrapybo.SecurityBo;
import com.lad.scrapybo.VideoBo;
import com.lad.service.IChatroomService;
import com.lad.service.ICircleService;
import com.lad.service.ICollectService;
import com.lad.service.ICommentService;
import com.lad.service.IDynamicService;
import com.lad.service.IFeedbackService;
import com.lad.service.IFriendsService;
import com.lad.service.IInforService;
import com.lad.service.ILocationService;
import com.lad.service.IMessageService;
import com.lad.service.INoteService;
import com.lad.service.IPartyService;
import com.lad.service.IReasonService;
import com.lad.service.ISearchService;
import com.lad.service.IShowService;
import com.lad.service.IThumbsupService;
import com.lad.service.IUserService;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.ERRORCODE;
import com.lad.util.JPushUtil;
import com.lad.util.MyException;
import com.lad.vo.CircleDisVo;
import com.lad.vo.CircleHeadVo;
import com.lad.vo.CircleHisVo;
import com.lad.vo.CircleVo;
import com.lad.vo.FriendsVo;
import com.lad.vo.NoteVo;
import com.lad.vo.PartyListVo;
import com.lad.vo.UserApplyVo;
import com.lad.vo.UserBaseVo;
import com.lad.vo.UserCircleVo;
import com.lad.vo.UserNoteVo;
import com.lad.vo.UserShowVo;
import com.lad.vo.UserStarVo;
import com.mongodb.CommandResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

@Api(value = "CircleController", description = "圈子相关接口")
@RestController
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

	@Autowired
	private IThumbsupService thumbsupService;

	@Autowired
	private ICommentService commentService;

	@Autowired
	private IChatroomService chatroomService;
	@Autowired
	private IInforService inforService;

	@Autowired
	private IMessageService messageService;

	@Autowired
	private AsyncController asyncController;

	@Autowired
	private IShowService showService;

	private String titlePush = "圈子通知";

	@ApiOperation("圈子中的演出关键字搜索接口(依据类型和公司)")
	@PostMapping("/show-keyword-search")
	public String showSearch(String keyword, int page, int limit, HttpServletRequest request,
			HttpServletResponse response) {
		String[] fields = { "showType", "company" };
		List<ShowBo> result = showService.findByList(fields, keyword, null, ShowBo.NEED, page, limit);
		return JSON.toJSONString(result);
	}

	/**
	 * 圈子添加成员时锁
	 */
	private String circleAddUserLock = "circleAdd";

	@ApiOperation("圈子创建")
	@PostMapping("/insert")
	public String isnert(@RequestParam(required = true) double px, @RequestParam(required = true) double py,
			@RequestParam(required = true) String name, @RequestParam(required = true) String tag,
			@RequestParam(required = true) String sub_tag, String description, String province, String city,
			String district, boolean isOpen, MultipartFile head_picture, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		// 每人最多创建三个群
		long circleNum = circleService.findCreateCricles(userBo.getId());
		if (circleNum >= userBo.getLevel() * 5) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_CREATE_MAX.getIndex(),
					ERRORCODE.CIRCLE_CREATE_MAX.getReason());
		}
		// 是否存在相同名字圈子
		CircleBo circle = circleService.findByTagAndName(name, tag, sub_tag);
		if (circle != null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_NAME_EXIST.getIndex(),
					ERRORCODE.CIRCLE_NAME_EXIST.getReason());
		}
		// 圈子名称不能和分类名称一样
		CircleTypeBo typeBo = circleService.findEsixtTagName(name);
		if (null != typeBo) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_NAME_EXIST.getIndex(),
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
		// 圈子头像
		if (head_picture != null) {
			long time = Calendar.getInstance().getTimeInMillis();
			String fileName = String.format("%s-%d-%s", userId, time, head_picture.getOriginalFilename());
			String path = CommonUtil.upload(head_picture, Constant.CIRCLE_HEAD_PICTURE_PATH, fileName, 0);
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

		userService.addUserLevel(userBo.getId(), 1, Constant.LEVEL_CIRCLE, 0);
		updateHistory(userBo.getId(), circleBo.getId(), locationService, circleService);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("circleid", circleBo.getId());
		return JSONObject.fromObject(map).toString();
	}

	@ApiOperation("圈子预创建时个人已拥有圈子信息返回")
	@PostMapping("/pre-create")
	public String preCreateCircle(HttpServletRequest request, HttpServletResponse response) {
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

	@ApiOperation("圈子重名校验")
	@PostMapping("/check-name")
	public String preCreateCircle(String name, String tag, String sub_tag, HttpServletRequest request,
			HttpServletResponse response) {
		CircleBo circleBo = circleService.findByTagAndName(name, tag, sub_tag);
		if (circleBo != null) {
			return "{\"ret\":1}";
		} else {
			return Constant.COM_RESP;
		}
	}

	@ApiOperation("圈子头像修改")
	@PostMapping("/head-picture")
	public String head_picture(@RequestParam("head_picture") MultipartFile file,
			@RequestParam(required = true) String circleid, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		String userId = userBo.getId();
		long time = Calendar.getInstance().getTimeInMillis();
		String fileName = String.format("%s-%d-%s", userId, time, file.getOriginalFilename());
		String path = CommonUtil.upload(file, Constant.CIRCLE_HEAD_PICTURE_PATH, fileName, 0);
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(), ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		if (!circleBo.getMasters().contains(userId) && !circleBo.getCreateuid().equals(userId)) {
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

	@ApiOperation("申请加入圈子")
	@ApiImplicitParams({ @ApiImplicitParam(name = "circleid", value = "圈子id", required = true, dataType = "string"),
			@ApiImplicitParam(name = "reason", value = "加入理由", dataType = "string"),
			@ApiImplicitParam(name = "isNotice", value = "是否通知好友", dataType = "boolean") })
	@PostMapping("/apply-insert")
	public String applyIsnert(@RequestParam(required = true) String circleid, String reason, boolean isNotice,
			HttpServletRequest request, HttpServletResponse response) {
		return applyIsnert(circleid, reason, isNotice, 0, null, request, response);
	}

	@ApiOperation("通过聚会前端申请加入圈子")
	@ApiImplicitParams({ @ApiImplicitParam(name = "circleid", value = "圈子id", required = true, dataType = "string"),
			@ApiImplicitParam(name = "reason", value = "加入理由", dataType = "string"),
			@ApiImplicitParam(name = "isNotice", value = "是否通知好友", dataType = "boolean"),
			@ApiImplicitParam(name = "addType", required = true, value = "聚会申请加入圈子为 1", dataType = "int"),
			@ApiImplicitParam(name = "partyid", value = "聚会id", required = true, dataType = "string") })
	@PostMapping("/party-apply-insert")
	public String applyIsnert(@RequestParam(required = true) String circleid, String reason, boolean isNotice,
			int addType, String partyid, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(), ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		updateHistory(userBo.getId(), circleid, locationService, circleService);
		HashSet<String> usersApply = circleBo.getUsersApply();
		if (usersApply == null) {
			usersApply = new LinkedHashSet<>();
		}
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
			reasonBo.setReasonType(0);
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
		circleBo.setUsersApply(usersApply);
		circleAddUserApply(circleid, usersApply);
		String content = String.format("“%s”申请加入您的圈子【%s】，快去审核吧", userBo.getUserName(), circleBo.getName());
		String path = "/circle/user-apply.do?circleid=" + circleid;
		JPushUtil.push(titlePush, content, path, circleBo.getCreateuid());
		addMessage(messageService, path, content, titlePush, circleBo.getCreateuid());
		HashSet<String> masters = circleBo.getMasters();
		if (!CommonUtil.isEmpty(masters)) {
			String[] pushUser = new String[masters.size()];
			masters.toArray(pushUser);
			JPushUtil.push(titlePush, content, path, pushUser);
			addMessage(messageService, path, content, titlePush, userBo.getId(), pushUser);
		}
		return Constant.COM_RESP;
	}

	@ApiOperation("自由加入圈子")
	@PostMapping("/free-insert")
	public String applyIsnert(@RequestParam(required = true) String circleid, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(), ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		if (circleBo.getUsers().contains(userBo.getId())) {
			return Constant.COM_RESP;
		}
		if (!circleBo.isOpen() || circleBo.isVerify()) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_NEED_VERIFY.getIndex(),
					ERRORCODE.CIRCLE_NEED_VERIFY.getReason());
		}
		updateHistory(userBo.getId(), circleid, locationService, circleService);
		HashSet<String> users = circleBo.getUsers();
		if (users.size() + 1 >= 500) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_USER_MAX.getIndex(),
					ERRORCODE.CIRCLE_USER_MAX.getReason());
		}
		ReasonBo reasonBo = reasonService.findByUserAdd(userBo.getId(), circleid);
		if (reasonBo == null) {
			reasonBo = new ReasonBo();
			reasonBo.setCircleid(circleid);
			reasonBo.setCreateuid(userBo.getId());
			reasonBo.setStatus(Constant.ADD_AGREE);
			reasonBo.setReasonType(0);
			reasonService.insert(reasonBo);
		} else {
			reasonService.updateApply(reasonBo.getId(), Constant.ADD_AGREE, "");
		}
		users.add(userBo.getId());
		circleAddUsers(circleid, users);
		userAddHis(userBo.getId(), circleBo.getId(), 1);
		return Constant.COM_RESP;
	}

	@ApiOperation("我的圈子信息")
	@PostMapping("/my-info")
	public String myInfo(HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		List<CircleBo> circleBoList = circleService.selectByuserid(userBo.getId());
		List<CircleVo> circleVoList = new LinkedList<CircleVo>();
		for (CircleBo circleBo : circleBoList) {
			CircleVo circleVo = new CircleVo();
			BeanUtils.copyProperties(circleBo, circleVo);
			circleVo.setUserAdd(1);
			// circleVo.setNotesSize(circleBo.getNoteSize());
			long noteSize = noteService.findNotesNum(circleBo.getId());
			circleVo.setNotesSize(noteSize);
			circleVoList.add(circleVo);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("circleVoList", circleVoList);
		return JSONObject.fromObject(map).toString();
	}

	@ApiOperation("申请加入圈子用户列表")
	@PostMapping("/user-apply")
	public String userApply(@RequestParam(required = true) String circleid, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(), ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		if (!circleBo.getCreateuid().equals(userBo.getId()) && circleBo.getMasters().contains(userBo.getId())) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_NOT_MASTER.getIndex(),
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
				UserApplyVo userApplyVo = new UserApplyVo();
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

	@ApiOperation("同意申请人加入圈子")
	@PostMapping("/user-apply-agree")
	public String userApplyAgree(@RequestParam(required = true) String circleid,
			@RequestParam(required = true) String userids, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(), ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		// 更新当前登录用户的圈子访问记录
		updateHistory(userBo.getId(), circleid, locationService, circleService);
		// 获取当前圈子用户列表
		String[] useridArr = CommonUtil.getIds(userids);
		HashSet<String> users = circleBo.getUsers();
		if (users.size() >= 500 || (users.size() + useridArr.length) > 500) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_USER_MAX.getIndex(),
					ERRORCODE.CIRCLE_USER_MAX.getReason());
		}
		if (!circleBo.getCreateuid().equals(userBo.getId()) && !circleBo.getMasters().contains(userBo.getId())) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_NOT_MASTER.getIndex(),
					ERRORCODE.CIRCLE_NOT_MASTER.getReason());
		}
		// 获取当前圈子的用户申请列表
		HashSet<String> usersApply = circleBo.getUsersApply();
		// 接受者集合
		List<String> accepts = new ArrayList<>();
		// 推送好友
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
					// 是否通过聚会页面加入圈子
					if (reasonBo.getAddType() == 1) {
						String party = String.format("/party/party-info.do?partyid=%s", reasonBo.getPartyid());
						JPushUtil.push(titlePush, content, party, userid);
						addMessage(messageService, party, content, titlePush, userid);
					} else {
						accepts.add(userid);
					}
				} else {
					reasonBo = new ReasonBo();
					reasonBo.setCircleid(circleid);
					reasonBo.setCreateuid(userid);
					reasonBo.setReasonType(0);
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
			JPushUtil.push(titlePush, content, path, userArr);
			addMessage(messageService, path, content, titlePush, userBo.getId(), userArr);
		}
		asyncController.pushToFriends(circleBo.getName(), path, pushFriends);
		return Constant.COM_RESP;
	}

	/**
	 * 用户加入圈子记录
	 * 
	 * @param userid
	 * @param circleid
	 * @param status
	 */
	private void userAddHis(String userid, String circleid, int status) {
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

	@ApiOperation("拒绝申请人加入圈子")
	@PostMapping("/user-apply-refuse")
	public String userApplyRefuse(@RequestParam(required = true) String circleid,
			@RequestParam(required = true) String userids, String refuse, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(), ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		updateHistory(userBo.getId(), circleid, locationService, circleService);
		if (!circleBo.getCreateuid().equals(userBo.getId()) && !circleBo.getMasters().contains(userBo.getId())) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_NOT_MASTER.getIndex(),
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
					return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_APPLY_USER_NULL.getIndex(),
							ERRORCODE.CIRCLE_APPLY_USER_NULL.getReason());
				}
				usersApply.remove(userid);
				usersRefuse.add(userid);
				// 添加历史拒绝为0?
				userAddHis(userid, circleid, 0);
				ReasonBo reasonBo = reasonService.findByUserAndCircle(userid, circleid, Constant.ADD_APPLY);
				if (reasonBo != null) {
					// 拒绝状态修改为2？
					reasonService.updateApply(reasonBo.getId(), Constant.ADD_REFUSE, refuse);
				}
			}
		}
		circleAddUsersRefuse(circleBo.getId(), usersApply, usersRefuse);
		return Constant.COM_RESP;
	}

	@ApiOperation("根据标签查找圈子列表")
	@PostMapping("/list")
	public String list(@RequestParam(required = true) String tag, @RequestParam(required = true) String sub_tag,
			@RequestParam(required = true) String category, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		List<CircleBo> list = circleService.selectByType(tag, sub_tag, category);
		return bo2vos(list, userBo);
	}

	@ApiOperation("圈子删除用户")
	@PostMapping("/delete-user")
	public String delete(@RequestParam(required = true) String circleid, @RequestParam(required = true) String userids,
			HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(), ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		updateHistory(userBo.getId(), circleid, locationService, circleService);
		String[] useridArr = CommonUtil.getIds(userids);
		// 圈主才能删除管理员
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
				if (!circleBo.getCreateuid().equals(userBo.getId())
						&& !circleBo.getMasters().contains(userBo.getId())) {
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
				removeUser++;
				userAddHis(userid, circleid, 2);
			}
		}
		if (removeUser > 0) {
			circleAddUsers(circleid, users);
			reasonService.removeUser(userids, circleid);
		}
		return Constant.COM_RESP;
	}

	@ApiOperation("圈子转让")
	@PostMapping("/transfer")
	public String transfer(@RequestParam(required = true) String circleid, @RequestParam(required = true) String userid,
			HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(), ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		updateHistory(userBo.getId(), circleid, locationService, circleService);
		if (!circleBo.getCreateuid().equals(userBo.getId())) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_NOT_MASTER.getIndex(),
					ERRORCODE.CIRCLE_NOT_MASTER.getReason());
		}
		if (userBo.getId().equals(userid)) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_SELF.getIndex(), ERRORCODE.CIRCLE_IS_SELF.getReason());
		}
		if (!circleBo.getUsers().contains(userid)) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_USER_NULL.getIndex(),
					ERRORCODE.CIRCLE_USER_NULL.getReason());
		}
		// 创建者默认为群主
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
		CircleHistoryBo historyBo = addCircleOperateHis(userid, circleid, userBo.getId(), "圈主转让通知", content);
		String path = "/circle/get-circle-his.do?circleid=" + historyBo.getId();
		JPushUtil.push("圈主转让通知", content, path, userid);
		addMessage(messageService, path, content, titlePush, userid);
		return Constant.COM_RESP;
	}

	/**
	 * 添加或删除管理员
	 * 
	 * @param circleid
	 * @param userids
	 * @param isAdd
	 *            true是添加， false 是删除
	 * @return
	 */
	@ApiOperation("圈子管理员操作")
	@PostMapping("/master")
	public String master(@RequestParam String circleid, @RequestParam String userids, @RequestParam boolean isAdd,
			HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(), ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		updateHistory(userBo.getId(), circleid, locationService, circleService);
		if (!circleBo.getCreateuid().equals(userBo.getId())) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_NOT_MASTER.getIndex(),
					ERRORCODE.CIRCLE_NOT_MASTER.getReason());
		}
		String[] ids = CommonUtil.getIds(userids);
		LinkedHashSet<String> masters = circleBo.getMasters();
		HashSet<String> users = circleBo.getUsers();
		UserBo creater = userService.getUser(circleBo.getCreateuid());
		String content = "";
		String title = "";
		if (isAdd) {
			// 判断圈子人数与管理员关系
			if (!hasMasterMax(users.size(), masters.size(), ids.length)) {
				return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_MASTER_MAX.getIndex(),
						ERRORCODE.CIRCLE_MASTER_MAX.getReason());
			}
			title = "设置管理员通知";
			content = String.format("“%s”将您设置为管理员", creater.getUserName());
			for (String id : ids) {
				if (users.contains(id)) {
					masters.add(id);
					CircleHistoryBo historyBo = addCircleOperateHis(id, circleid, userBo.getId(), title, content);
					String path = "/circle/get-circle-his.do?circleid=".concat(historyBo.getId());
					JPushUtil.push(title, content, path, id);
					addMessage(messageService, path, content, title, id);
				}
			}

		} else {
			title = "取消管理员通知";
			content = String.format("“%s”已取消您的管理员", creater.getUserName());
			for (String id : ids) {
				if (masters.contains(id)) {
					masters.remove(id);
					CircleHistoryBo historyBo = addCircleOperateHis(id, circleid, userBo.getId(), title, content);
					String path = "/circle/get-circle-his.do?circleid=".concat(historyBo.getId());
					JPushUtil.push(title, content, path, id);
					addMessage(messageService, path, content, title, id);
				}
			}
		}
		circleService.updateMaster(circleBo);
		return Constant.COM_RESP;
	}

	/**
	 * 判断圈子管理员是否已到上线
	 * 
	 * @param userNum
	 * @param masterNum
	 * @param addNum
	 * @return
	 */
	private boolean hasMasterMax(int userNum, int masterNum, int addNum) {
		if (userNum <= 100 && masterNum + addNum > 5) {
			return false;
		}
		if (userNum <= 250 && masterNum + addNum > 10) {
			return false;
		}
		if (userNum <= 500 && masterNum + addNum > 15) {
			return false;
		}
		return true;
	}

	@ApiOperation("退出圈子")
	@PostMapping("/quit")
	public String delete(@RequestParam(required = true) String circleid, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(), ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		if (circleBo.getCreateuid().equals(userBo.getId())) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_NOT_QUIT.getIndex(),
					ERRORCODE.CIRCLE_NOT_QUIT.getReason());
		}
		LinkedHashSet<String> masters = circleBo.getMasters();
		if (masters.contains(userBo.getId())) {
			masters.remove(userBo.getId());
			circleBo.setMasters(masters);
			circleService.updateMaster(circleBo);
		}
		// 删除置顶的圈子
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

	@ApiOperation("我的圈子列表")
	@PostMapping("/my-circles")
	public String myCircles(int page, int limit, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		// 置顶的圈子id
		List<String> topCircles = userBo.getCircleTops();

		if (page < 1 || limit < 0) {
			return Constant.COM_FAIL_RESP;
		}
		// 放入合集的置顶圈子
		int current = 0;
		List<CircleVo> voList = new LinkedList<>();
		if (!topCircles.isEmpty() && page <= 1) {
			List<CircleBo> tops = circleService.findCirclesInList(topCircles);
			// 因置顶圈子不能再数据库实现分页，
			for (String top : topCircles) {
				// 由于mongo查询结果不是按照list的顺序，在程序中再次处理顺序
				for (CircleBo circleBo : tops) {
					if (top.equals(circleBo.getId())) {
						if (circleBo.getTotal() == 0) {
							int number = noteService.selectPeopleNum(circleBo.getId());
							circleBo.setTotal(number);
							circleService.updateTotal(circleBo.getId(), number);
						}
						current++;
						voList.add(bo2vo(circleBo, userBo, 1));
						tops.remove(circleBo);
						break;
					}
				}
			}
		}
		if (current < limit || page > 1) {
			List<CircleAddBo> addBoArr = circleService.findApplyCircleAddByUid(userBo.getId());
			List<CircleBo> circleBos = new ArrayList<>();
			for (CircleAddBo circleAddBo : addBoArr) {
				// circleAddBo.getCircleid();
				CircleBo selectById = circleService.selectById(circleAddBo.getCircleid());
				if (!org.springframework.util.StringUtils.isEmpty(selectById)) {
					circleBos.add(selectById);
				}

			}
			// List<CircleBo> circleBos =
			// circleService.findMyCircles(userBo.getId(), page, limit);
			// 筛选出置顶的圈子
			for (CircleBo circleBo : circleBos) {
				if (topCircles.contains(circleBo.getId())) {
					continue;
				}
				if (circleBo.getTotal() == 0) {
					int number = noteService.selectPeopleNum(circleBo.getId());
					circleBo.setTotal(number);
					circleService.updateTotal(circleBo.getId(), number);
				}
				voList.add(bo2vo(circleBo, userBo, 0));
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("circleVoList", voList);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 返回10个热门圈子（以圈子内人数排序，人数最多的10个圈子）
	 */
	@ApiOperation("猜你喜欢圈子列表")
	@PostMapping("/guess-you-like")
	public String youLike(String city, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		LocationBo locationBo = locationService.getLocationBoByUserid(userBo.getId());
		List<CircleBo> circleBos = circleService.selectUsersLike(userBo.getId(), city, locationBo.getPosition(), 5000);
		return bo2vos(circleBos, userBo);
	}

	/**
	 * 圈子详情
	 */
	@ApiOperation("圈子详情信息")
	@RequestMapping(value = "/circle-info", method = { RequestMethod.GET, RequestMethod.POST })
	public String info(String circleid, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		int userAdd = 0;
		if (userBo != null) {
			CircleAddBo addBo = circleService.findHisByUserAndCircle(userBo.getId(), circleid);
			userAdd = addBo != null ? addBo.getStatus() : 0;
			// 更新访问记录
			updateHistory(userBo.getId(), circleid, locationService, circleService);
			updateCircieUnReadZero(userBo.getId(), circleid);
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(), ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		// 圈子访问
		updateCircleHot(circleService, redisServer, circleid, 1, Constant.CIRCLE_VISIT);
		CircleVo circleVo = new CircleVo();
		BeanUtils.copyProperties(circleBo, circleVo);
		circleVo.setName(circleBo.getName());
		circleVo.setUsersSize(circleBo.getUsers().size());
		long noteSize = noteService.findNotesNum(circleBo.getId());
		circleVo.setNotesSize(noteSize);
		circleVo.setUserAdd(userAdd);
		LinkedHashSet<String> masters = circleBo.getMasters();
		// 管理员
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
		// 圈主
		UserBo hostBo = userService.getUser(circleBo.getCreateuid());
		UserBaseVo userHostVo = new UserBaseVo();
		BeanUtils.copyProperties(hostBo, userHostVo);
		userHostVo.setRole(2);

		long partyNum = partyService.getCirclePartyNum(circleid);

		CircleNoticeBo lastNotice = circleService.findLastNotice(circleid, 0);
		boolean isRead = false;
		JSONObject jsonObject = new JSONObject();
		if (lastNotice != null) {
			LinkedHashSet<String> readUsers = lastNotice.getReadUsers();
			isRead = readUsers.contains(userBo.getId());
			jsonObject.put("noticeid", lastNotice.getId());
			jsonObject.put("noticeTitle", lastNotice.getTitle());
			jsonObject.put("notice", lastNotice.getContent());
			jsonObject.put("noticeTime", lastNotice.getCreateTime());
			jsonObject.put("image", lastNotice.getImages());
			jsonObject.put("readNum", lastNotice.getReadUsers().size());
			UserBo user = userService.getUser(lastNotice.getCreateuid());
			if (user != null) {
				UserBaseVo userBaseVo = new UserBaseVo();
				BeanUtils.copyProperties(user, userBaseVo);
				jsonObject.put("noticeUser", userBaseVo);
			}
		}

		// 清零访问
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("ret", 0);
		map.put("creater", userHostVo);
		map.put("masters", mastersList);
		map.put("userVos", userList);
		map.put("circleVo", circleVo);
		map.put("partyNum", partyNum);
		map.put("noticeRead", isRead);
		map.put("noticeTotal", circleService.findNoticeTotal(circleid, 0));
		map.put("notice", jsonObject);
		if (userBo != null) {
			List<CircleNoticeBo> unReadNotices = circleService.findUnReadNotices(userBo.getId(), circleid, 0);
			map.put("unReadNoticeNum", unReadNotices == null ? 0 : unReadNotices.size());
		}
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 红人列表，总榜及周榜
	 */
	@ApiOperation("圈子红人列表")
	@PostMapping("/red-star-list")
	public String redTopTotal(String circleid, HttpServletRequest request, HttpServletResponse response) {
		// 如果登录就显示浏览记录

		UserBo userBo = getUserLogin(request);
		if (null != userBo) {
			updateHistory(userBo.getId(), circleid, locationService, circleService);
		}

		Date currentDate = new Date();
		int weekNo = CommonUtil.getWeekOfYear(currentDate);
		int year = CommonUtil.getYear(currentDate);

		List<RedstarBo> total = userService.findRedUserTotal(circleid);

		List<RedstarBo> week = userService.findRedUserWeek(circleid, weekNo, year);

		List<UserStarVo> totals = getStar(total);

		List<UserStarVo> weeks = getStar(week);

		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("total", totals);
		map.put("week", weeks);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 置顶圈子
	 */
	@ApiOperation("置顶圈子")
	@PostMapping("/set-top")
	public String setTopCircle(String circleid, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (null == circleBo) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(), ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		updateHistory(userBo.getId(), circleid, locationService, circleService);
		List<String> topList = userBo.getCircleTops();
		topList.remove(circleid);
		topList.add(0, circleid);
		userService.updateTopCircles(userBo.getId(), topList);
		return Constant.COM_RESP;
	}

	/**
	 * 取消置顶圈子
	 */
	@ApiOperation("取消圈子置顶")
	@PostMapping("/cancel-top")
	public String cancelTopCircle(String circleid, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (null == circleBo) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(), ERRORCODE.CIRCLE_IS_NULL.getReason());
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
	@ApiOperation("根据城市和关键字搜索圈子,默认10条返回")
	@PostMapping("/search")
	public String search(String keyword, String city, HttpServletRequest request, HttpServletResponse response) {
		return searchKeyword(keyword, city, 1, 10, request, response);
	}

	/**
	 * 搜索圈子
	 */
	@ApiOperation("根据城市和关键字搜索圈子,具有分页")
	@PostMapping("/search-keyword")
	public String searchKeyword(String keyword, String city, int page, int limit, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (StringUtils.isNotEmpty(keyword)) {
			List<CircleBo> circleBos = circleService.findBykeyword(keyword, city, page, limit);
			saveKeyword(keyword);
			return bo2vos(circleBos, userBo);
		}
		return Constant.COM_RESP;
	}

	private void saveKeyword(String keyword) {
		CircleTypeBo typeBo = circleService.findByName(keyword, 2, CircleTypeBo.CIRCLE);
		if (typeBo != null) {
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
	@ApiOperation("根据类型获取圈子")
	@ApiImplicitParams({ @ApiImplicitParam(name = "tag", value = "圈子一级分类", paramType = "query", dataType = "string"),
			@ApiImplicitParam(name = "sub_tag", value = "圈子二级分类", paramType = "query", dataType = "string"),
			@ApiImplicitParam(name = "city", value = "圈子置顶搜索城市", paramType = "query", dataType = "string"),
			@ApiImplicitParam(name = "page", value = "分页page", paramType = "query", dataType = "int"),
			@ApiImplicitParam(name = "limit", value = "limit条数", paramType = "query", dataType = "int") })
	@PostMapping("/get-by-type")
	public String getByType(String tag, String sub_tag, String city, int page, int limit, HttpServletRequest request,
			HttpServletResponse response) {
		List<CircleBo> circleBos = circleService.findByType(tag, sub_tag, city, page, limit);
		if (StringUtils.isNotEmpty(sub_tag)) {
			saveKeyword(sub_tag);
		}
		if (StringUtils.isNotEmpty(tag)) {
			saveKeyword(tag);
		}
		UserBo userBo = getUserLogin(request);
		boolean isLogin = userBo != null;
		List<CircleVo> listVo = new LinkedList<>();
		for (CircleBo circleBo : circleBos) {
			CircleVo circleVo = new CircleVo();
			BeanUtils.copyProperties(circleBo, circleVo);
			circleVo.setId(circleBo.getId());
			circleVo.setName(circleBo.getName());
			long noteSize = noteService.findNotesNum(circleBo.getId());
			circleVo.setNotesSize(noteSize);
			circleVo.setUsersSize(circleBo.getUsers().size());
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
	@ApiOperation("获取圈子创建人")
	@PostMapping("/get-creater")
	public String getCreater(String circleid, HttpServletRequest request, HttpServletResponse response) {

		CircleBo circleBo = circleService.selectById(circleid);
		if (null == circleBo) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(), ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		// 圈主
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
	@ApiOperation("获取圈子管理员列表")
	@PostMapping("/get-master")
	public String getMaster(String circleid, HttpServletRequest request, HttpServletResponse response) {
		CircleBo circleBo = circleService.selectById(circleid);
		if (null == circleBo) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(), ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		LinkedHashSet<String> masters = circleBo.getMasters();
		// 管理员
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
	@ApiOperation("获取圈子分类列表")
	@RequestMapping(value = "/circle-type", method = { RequestMethod.GET, RequestMethod.POST })
	public String circleType(HttpServletRequest request, HttpServletResponse response) {

		List<CircleTypeBo> typeBos = circleService.selectByLevel(1, CircleTypeBo.CIRCLE);
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
	 * 添加圈子分类
	 */
	@ApiOperation("添加圈子分类")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "name", value = "圈子分类名称", required = true, paramType = "query", dataType = "string"),
			@ApiImplicitParam(name = "parent", value = "父分类，没有则为空", paramType = "query", dataType = "string"),
			@ApiImplicitParam(name = "level", value = "分类等级", paramType = "query", dataType = "int") })
	@PostMapping("/add-circle-type")
	public String addCircleType(String name, String parent, int level, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		CircleTypeBo typeBo = circleService.findByName(name, level, CircleTypeBo.CIRCLE);
		if (typeBo == null) {
			typeBo = new CircleTypeBo();
			typeBo.setCategory(name);
			typeBo.setLevel(level);
			if (StringUtils.isNotEmpty(parent)) {
				typeBo.setPreCateg(parent);
			}
			typeBo.setType(CircleTypeBo.CIRCLE);
			typeBo.setCreateuid(userBo.getId());
		} else {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_TYPE_EXIST.getIndex(),
					ERRORCODE.CIRCLE_TYPE_EXIST.getReason());
		}
		return Constant.COM_RESP;
	}

	/**
	 * 更多圈子时，获取所有分类
	 */
	@ApiOperation("获取所有圈子分类列表")
	@RequestMapping(value = "/circle-type-search", method = { RequestMethod.GET, RequestMethod.POST })
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
	@ApiOperation("附近活跃人员")
	@PostMapping("/near-people")
	public String nearPeopel(String circleid, double px, double py, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		String userid = userBo != null ? userBo.getId() : "";
		double[] position = new double[] { px, py };
		GeoResults<CircleHistoryBo> results = circleService.findNearPeopleDis(circleid, userid, position, 10000);

		JSONArray array = new JSONArray();
		DecimalFormat df = new DecimalFormat("###.00");
		for (GeoResult<CircleHistoryBo> result : results) {
			System.out.println(result.getContent());
			UserBo temp = userService.getUser(result.getContent().getUserid());
			if (temp != null) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", temp.getId());
				jsonObject.put("userName", temp.getUserName());
				jsonObject.put("phone", temp.getPhone());
				jsonObject.put("sex", temp.getSex());
				jsonObject.put("headPictureName", temp.getHeadPictureName());
				jsonObject.put("birthDay", temp.getBirthDay());
				jsonObject.put("personalizedSignature", temp.getPersonalizedSignature());
				jsonObject.put("level", temp.getLevel());
				double dis = Double.parseDouble(df.format(result.getDistance().getValue()));
				jsonObject.put("distance", dis);
				array.add(jsonObject);
			}
		}
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("userVoList", array);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 附近圈子
	 */
	@ApiOperation("附近圈子列表")
	@PostMapping("/near-circle")
	public String nearPeopel(double px, double py, int page, int limit, HttpServletRequest request,
			HttpServletResponse response) {
		double[] position = new double[] { px, py };
		// 未登录情况
		UserBo userBo = getUserLogin(request);
		String userid = userBo != null ? userBo.getId() : "";

		// 通过命令获取结果
		CommandResult commanResult = circleService.findNearCircleByCommond(userid, position, 10000, page, limit);
		// 将结果转为json对象
		JSONObject jsonEntity = JSONObject.fromObject(commanResult.toJson());
		// 获取对象中的有用实体,是一个list
		List<Map<String, Object>> entityList = (List<Map<String, Object>>) jsonEntity.get("results");
		List<CircleVo> listVo = new LinkedList<>();

		for (Map<String, Object> map : entityList) {
			// map
			// {"dis":2145252,"obj":{"name":"zhangsa","position":[13423,215]}}
			Map<String, Object> entryMap = (Map<String, Object>) map.get("obj");
			CircleBo circleBo = new CircleBo();
			if (JSONArray.toCollection((JSONArray) entryMap.get("users")).contains(userid)) {
				continue;
			}
			for (Entry<String, Object> entry : entryMap.entrySet()) {
				// 这是id 获取id
				if ("_id".equals(entry.getKey())) {
					String[] split = entry.getValue().toString().split("\"");
					circleBo.setId(split[3]);
					continue;
				}
				if ("position".equals(entry.getKey())) {
					JSONArray jsonArray = (JSONArray) entry.getValue();
					double[] resultPosition = { (double) jsonArray.get(0), (double) jsonArray.get(1) };
					circleBo.setPosition(resultPosition);
					continue;
				}
				if ("name".equals(entry.getKey())) {
					circleBo.setName((String) entry.getValue());
					continue;
				}
				if ("tag".equals(entry.getKey())) {
					circleBo.setTag((String) entry.getValue());
					continue;
				}
				if ("sub_tag".equals(entry.getKey())) {
					circleBo.setSub_tag((String) entry.getValue());
					continue;
				}
				if ("headPicture".equals(entry.getKey())) {
					circleBo.setHeadPicture((String) entry.getValue());
					continue;
				}
				if ("createuid".equals(entry.getKey())) {
					circleBo.setCreateuid((String) entry.getValue());
					continue;
				}
				if ("category".equals(entry.getKey())) {
					circleBo.setCategory((String) entry.getValue());
					continue;
				}
				if ("description".equals(entry.getKey())) {
					circleBo.setDescription((String) entry.getValue());
					continue;
				}
				if ("noticeTitle".equals(entry.getKey())) {
					circleBo.setNoticeTitle((String) entry.getValue());
					continue;
				}
				if ("notice".equals(entry.getKey())) {
					circleBo.setNotice((String) entry.getValue());
					continue;
				}
				if ("noticeUserid".equals(entry.getKey())) {
					circleBo.setNoticeUserid((String) entry.getValue());
					continue;
				}
				if ("province".equals(entry.getKey())) {
					circleBo.setProvince((String) entry.getValue());
					continue;
				}
				if ("city".equals(entry.getKey())) {
					circleBo.setCity((String) entry.getValue());
					continue;
				}
				if ("district".equals(entry.getKey())) {
					circleBo.setDistrict((String) entry.getValue());
					continue;
				}

				// boolean
				if ("takeShow".equals(entry.getKey())) {
					circleBo.setTakeShow((boolean) entry.getValue());
					continue;
				}
				if ("isOpen".equals(entry.getKey())) {
					circleBo.setOpen((boolean) entry.getValue());
					continue;
				}
				if ("isVerify".equals(entry.getKey())) {
					circleBo.setVerify((boolean) entry.getValue());
					continue;
				}

				// collection
				if ("users".equals(entry.getKey())) {
					JSONArray jsonArray = (JSONArray) entry.getValue();
					HashSet<String> users = new HashSet<String>(JSONArray.toCollection(jsonArray));
					circleBo.setUsers(users);
					continue;
				}

				if ("usersApply".equals(entry.getKey())) {
					if (entry.getValue().getClass().toString().equals("class net.sf.json.JSONNull")) {
						circleBo.setUsersApply(new HashSet<String>());
					}else{
						JSONArray jsonArray = (JSONArray) entry.getValue();
						HashSet<String> users = new HashSet<String>(JSONArray.toCollection(jsonArray));
						circleBo.setUsersApply(users);
					}
					continue;
				}

				if ("usersRefuse".equals(entry.getKey())) {
					if (entry.getValue().getClass().toString().equals("class net.sf.json.JSONNull")) {
						circleBo.setUsersRefuse(new HashSet<String>());
					}else{
						JSONArray jsonArray = (JSONArray) entry.getValue();
						HashSet<String> users = new HashSet<String>(JSONArray.toCollection(jsonArray));
						circleBo.setUsersRefuse(users);
					}
					continue;
				}

				if ("masters".equals(entry.getKey())) {
					JSONArray jsonArray = (JSONArray) entry.getValue();
					LinkedHashSet<String> masters = new LinkedHashSet<>(JSONArray.toCollection(jsonArray));
					circleBo.setMasters(masters);
					continue;
				}

				// double
				if ("hotNum".equals(entry.getKey())) {
					if (entry.getValue().getClass().toString().equals("java.lang.Double")) {
						double hotNum = (double) entry.getValue();
						circleBo.setHotNum(hotNum);
					}
					if (entry.getValue().getClass().toString().equals("net.sf.json.JSONObject")) {
						JSONObject value = (JSONObject) entry.getValue();
						double hotNum = value.getDouble("$numberLong");
						circleBo.setHotNum(hotNum);
					}
					continue;
				}

				// Date
				if ("noticeTime".equals(entry.getKey())) {
					JSONObject value = (JSONObject) entry.getValue();
					long time = value.getLong("$date");
					Date date = new Date(time);
					circleBo.setNoticeTime(date);
					continue;
				}

				// int
				if ("usernum".equals(entry.getKey())) {
					if (entry.getValue().getClass().toString().equals("java.lang.Double")) {
						int intValue = new Double((Double) entry.getValue()).intValue();
						circleBo.setUsernum(intValue);
					} else if (entry.getValue().getClass().toString().equals("java.lang.Integer")) {
						circleBo.setUsernum((int) entry.getValue());
					} else if (entry.getValue().getClass().toString().equals("net.sf.json.JSONObject")) {
						JSONObject value = (JSONObject) entry.getValue();
						int intValue = new Double(value.getDouble("$numberLong")).intValue();
						circleBo.setUsernum(intValue);
					}
					continue;
				}
				if ("noteSize".equals(entry.getKey())) {
					if (entry.getValue().getClass().toString().equals("java.lang.Double")) {
						int intValue = new Double((Double) entry.getValue()).intValue();
						circleBo.setNoteSize(intValue);
					} else if (entry.getValue().getClass().toString().equals("java.lang.Integer")) {
						circleBo.setNoteSize((int) entry.getValue());
					} else if (entry.getValue().getClass().toString().equals("net.sf.json.JSONObject")) {
						JSONObject value = (JSONObject) entry.getValue();
						int intValue = new Double(value.getDouble("$numberLong")).intValue();
						circleBo.setNoteSize(intValue);
					}
					continue;
				}
				if ("total".equals(entry.getKey())) {
					if (entry.getValue().getClass().toString().equals("java.lang.Double")) {
						int intValue = new Double((Double) entry.getValue()).intValue();
						circleBo.setTotal(intValue);
					} else if (entry.getValue().getClass().toString().equals("java.lang.Integer")) {
						circleBo.setTotal((int) entry.getValue());
					} else if (entry.getValue().getClass().toString().equals("net.sf.json.JSONObject")) {
						JSONObject value = (JSONObject) entry.getValue();
						int intValue = new Double(value.getDouble("$numberLong")).intValue();
						circleBo.setTotal(intValue);
					}
					continue;
				}
				if ("commentNum".equals(entry.getKey())) {
					if (entry.getValue().getClass().toString().equals("java.lang.Double")) {
						int intValue = new Double((Double) entry.getValue()).intValue();
						circleBo.setCommentNum(intValue);
					} else if (entry.getValue().getClass().toString().equals("java.lang.Integer")) {
						circleBo.setCommentNum((int) entry.getValue());
					} else if (entry.getValue().getClass().toString().equals("net.sf.json.JSONObject")) {
						JSONObject value = (JSONObject) entry.getValue();
						int intValue = new Double(value.getDouble("$numberLong")).intValue();
						circleBo.setCommentNum(intValue);
					}
					continue;
				}
				if ("transmitNum".equals(entry.getKey())) {
					if (entry.getValue().getClass().toString().equals("java.lang.Double")) {
						int intValue = new Double((Double) entry.getValue()).intValue();
						circleBo.setTransmitNum(intValue);
					} else if (entry.getValue().getClass().toString().equals("java.lang.Integer")) {
						circleBo.setTransmitNum((int) entry.getValue());
					} else if (entry.getValue().getClass().toString().equals("net.sf.json.JSONObject")) {
						JSONObject value = (JSONObject) entry.getValue();
						int intValue = new Double(value.getDouble("$numberLong")).intValue();
						circleBo.setTransmitNum(intValue);
					}
					continue;
				}
				if ("visitNum".equals(entry.getKey())) {
					if (entry.getValue().getClass().toString().equals("java.lang.Double")) {
						int intValue = new Double((Double) entry.getValue()).intValue();
						circleBo.setVisitNum(intValue);
					} else if (entry.getValue().getClass().toString().equals("java.lang.Integer")) {
						circleBo.setVisitNum((int) entry.getValue());
					} else if (entry.getValue().getClass().toString().equals("net.sf.json.JSONObject")) {
						JSONObject value = (JSONObject) entry.getValue();
						int intValue = new Double(value.getDouble("$numberLong")).intValue();
						circleBo.setVisitNum(intValue);
					}
					continue;
				}
				if ("thumpNum".equals(entry.getKey())) {
					if (entry.getValue().getClass().toString().equals("java.lang.Double")) {
						int intValue = new Double((Double) entry.getValue()).intValue();
						circleBo.setThumpNum(intValue);
					} else if (entry.getValue().getClass().toString().equals("java.lang.Integer")) {
						circleBo.setThumpNum((int) entry.getValue());
					} else if (entry.getValue().getClass().toString().equals("net.sf.json.JSONObject")) {
						JSONObject value = (JSONObject) entry.getValue();
						int intValue = new Double(value.getDouble("$numberLong")).intValue();
						circleBo.setThumpNum(intValue);
					}
					continue;
				}
				if ("partyNum".equals(entry.getKey())) {
					if (entry.getValue().getClass().toString().equals("java.lang.Double")) {
						int intValue = new Double((Double) entry.getValue()).intValue();
						circleBo.setPartyNum(intValue);
					} else if (entry.getValue().getClass().toString().equals("java.lang.Integer")) {
						circleBo.setPartyNum((int) entry.getValue());
					} else if (entry.getValue().getClass().toString().equals("net.sf.json.JSONObject")) {
						JSONObject value = (JSONObject) entry.getValue();
						int intValue = new Double(value.getDouble("$numberLong")).intValue();
						circleBo.setPartyNum(intValue);
					}
					continue;
				}
				if ("noteNum".equals(entry.getKey())) {
					if (entry.getValue().getClass().toString().equals("java.lang.Double")) {
						int intValue = new Double((Double) entry.getValue()).intValue();
						circleBo.setNoteNum(intValue);
					} else if (entry.getValue().getClass().toString().equals("java.lang.Integer")) {
						circleBo.setNoteNum((int) entry.getValue());
					} else if (entry.getValue().getClass().toString().equals("net.sf.json.JSONObject")) {
						JSONObject value = (JSONObject) entry.getValue();
						int intValue = new Double(value.getDouble("$numberLong")).intValue();
						circleBo.setNoteNum(intValue);
					}
					continue;
				}
				if ("partyVisit".equals(entry.getKey())) {
					if (entry.getValue().getClass().toString().equals("java.lang.Double")) {
						int intValue = new Double((Double) entry.getValue()).intValue();
						circleBo.setPartyVisit(intValue);
					} else if (entry.getValue().getClass().toString().equals("java.lang.Integer")) {
						circleBo.setPartyVisit((int) entry.getValue());
					} else if (entry.getValue().getClass().toString().equals("net.sf.json.JSONObject")) {
						JSONObject value = (JSONObject) entry.getValue();
						int intValue = new Double(value.getDouble("$numberLong")).intValue();
						circleBo.setPartyVisit(intValue);
					}
					continue;
				}
				if ("partyThump".equals(entry.getKey())) {
					if (entry.getValue().getClass().toString().equals("java.lang.Double")) {
						int intValue = new Double((Double) entry.getValue()).intValue();
						circleBo.setPartyThump(intValue);
					} else if (entry.getValue().getClass().toString().equals("java.lang.Integer")) {
						circleBo.setPartyThump((int) entry.getValue());
					} else if (entry.getValue().getClass().toString().equals("net.sf.json.JSONObject")) {
						JSONObject value = (JSONObject) entry.getValue();
						int intValue = new Double(value.getDouble("$numberLong")).intValue();
						circleBo.setPartyThump(intValue);
					}
					continue;
				}
				if ("partyShare".equals(entry.getKey())) {
					if (entry.getValue().getClass().toString().equals("java.lang.Double")) {
						int intValue = new Double((Double) entry.getValue()).intValue();
						circleBo.setPartyShare(intValue);
					} else if (entry.getValue().getClass().toString().equals("java.lang.Integer")) {
						circleBo.setPartyShare((int) entry.getValue());
					} else if (entry.getValue().getClass().toString().equals("net.sf.json.JSONObject")) {
						JSONObject value = (JSONObject) entry.getValue();
						int intValue = new Double(value.getDouble("$numberLong")).intValue();
						circleBo.setPartyShare(intValue);
					}
					continue;
				}
			}
			CircleDisVo circleVo = new CircleDisVo();
			BeanUtils.copyProperties(circleBo, circleVo);
			circleVo.setId(circleBo.getId());
			circleVo.setName(circleBo.getName());
			long noteSize = noteService.findNotesNum(circleBo.getId());
			circleVo.setNotesSize(noteSize);
			circleVo.setUsersSize(circleBo.getUsers().size());
			circleVo.setTop(0);
			Double dis = (Double) map.get("dis");
			circleVo.setDistance(dis);
			if (null != userBo) {
				ReasonBo reasonBo = reasonService.findByUserAndCircle(userBo.getId(), circleBo.getId(),
						Constant.ADD_AGREE);
				// 圈子未读数量信息
				if (reasonBo != null) {
					circleVo.setUnReadNum(reasonBo.getUnReadNum());
				}
				// 查找圈子加入历史
				CircleAddBo addBo = circleService.findHisByUserAndCircle(userBo.getId(), circleBo.getId());
				if (null != addBo) {
					circleVo.setUserAdd(addBo.getStatus());
				}
			}
			listVo.add(circleVo);
		}

		/*
		 * GeoResults<CircleBo> circleBos = circleService.findNearCircle(userid,
		 * position, 10000, page, limit); List<CircleVo> listVo = new
		 * LinkedList<>(); DecimalFormat df = new DecimalFormat("###.00"); for
		 * (GeoResult<CircleBo> result : circleBos) { CircleBo circleBo =
		 * result.getContent(); CircleDisVo circleVo = new CircleDisVo();
		 * BeanUtils.copyProperties(circleBo, circleVo);
		 * circleVo.setId(circleBo.getId());
		 * circleVo.setName(circleBo.getName()); //
		 * circleVo.setNotesSize(circleBo.getNoteSize()); long noteSize =
		 * noteService.findNotesNum(circleBo.getId());
		 * circleVo.setNotesSize(noteSize);
		 * circleVo.setUsersSize(circleBo.getUsers().size());
		 * circleVo.setTop(0); if (null != userBo) { ReasonBo reasonBo =
		 * reasonService.findByUserAndCircle(userBo.getId(), circleBo.getId(),
		 * Constant.ADD_AGREE); // 圈子未读数量信息 if (reasonBo != null) {
		 * circleVo.setUnReadNum(reasonBo.getUnReadNum()); } // 查找圈子加入历史
		 * CircleAddBo addBo =
		 * circleService.findHisByUserAndCircle(userBo.getId(),
		 * circleBo.getId()); if (null != addBo) {
		 * circleVo.setUserAdd(addBo.getStatus()); } } double dis =
		 * Double.parseDouble(df.format(result.getDistance().getValue()));
		 * circleVo.setDistance(dis); listVo.add(circleVo); }
		 */
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("circleVoList", listVo);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 判断当前用户在圈子中的身份
	 */
	@ApiOperation("当前用户在圈子中的身份")
	@PostMapping("/circle-role")
	public String ciecleRle(String circleid, HttpServletRequest request, HttpServletResponse response) {
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(), ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		// 判断当前用户是否已经登录
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
	@ApiOperation("圈子中的用户列表")
	@PostMapping("/persons")
	public String cieclePerson(String circleid, HttpServletRequest request, HttpServletResponse response) {
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(), ERRORCODE.CIRCLE_IS_NULL.getReason());
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
			} else if (masters.contains(userId)) {
				userCircleVo.setRole(1);
			} else {
				userCircleVo.setRole(0);
			}
			userCircleVo.setMaxCircleNum(userCircleVo.getLevel() * 5);
			userCircleVo.setHasCircleNum((int) circleNums);
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
	@ApiOperation("修改圈子名称")
	@PostMapping("/update-name")
	public String updateName(@RequestParam String circleid, @RequestParam String name, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(), ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		if (circleBo.getMasters().contains(userBo.getId()) || circleBo.getCreateuid().equals(userBo.getId())) {
			if (StringUtils.isNotEmpty(name)) {
				CircleBo circle = circleService.findByTagAndName(name, circleBo.getTag(), circleBo.getSub_tag());
				if (circle != null) {
					return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_NAME_EXIST.getIndex(),
							ERRORCODE.CIRCLE_NAME_EXIST.getReason());
				}
				circleService.updateCircleName(circleid, name);
			} else {
				return Constant.COM_FAIL_RESP;
			}
		} else {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_NOT_MASTER.getIndex(),
					ERRORCODE.CIRCLE_NOT_MASTER.getReason());
		}
		return Constant.COM_RESP;
	}

	/**
	 * 是否允许加入
	 */
	@ApiOperation("修改圈子开放状态")
	@PostMapping("/open")
	public String updateOpen(@RequestParam String circleid, boolean open, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(), ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		if (circleBo.getCreateuid().equals(userBo.getId()) || circleBo.getMasters().contains(userBo.getId())) {
			circleService.updateOpen(circleid, open);
		} else {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_MASTER_NULL.getIndex(),
					ERRORCODE.CIRCLE_MASTER_NULL.getReason());
		}
		return Constant.COM_RESP;
	}

	/**
	 * 是否允许加入
	 */
	@ApiOperation("修改圈子验证状态")
	@PostMapping("/verify")
	public String updateVerify(@RequestParam String circleid, boolean verify, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(), ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		if (circleBo.getCreateuid().equals(userBo.getId()) || circleBo.getMasters().contains(userBo.getId())) {
			circleService.updateisVerify(circleid, verify);
		} else {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_MASTER_NULL.getIndex(),
					ERRORCODE.CIRCLE_MASTER_NULL.getReason());
		}
		return Constant.COM_RESP;
	}

	/**
	 * 是否允许加入
	 */
	@ApiOperation("修改圈子接演出状态")
	@ApiImplicitParams({ @ApiImplicitParam(name = "circleid", required = true, value = "圈子id", dataType = "string"),
			@ApiImplicitParam(name = "takeShow", required = true, value = "圈子是否承接演出", dataType = "boolean") })
	@PostMapping("/take-show")
	public String updateShow(String circleid, boolean takeShow, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(), ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		if (circleBo.getCreateuid().equals(userBo.getId()) || circleBo.getMasters().contains(userBo.getId())) {
			circleService.updateTakeShow(circleid, takeShow);
		} else {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_MASTER_NULL.getIndex(),
					ERRORCODE.CIRCLE_MASTER_NULL.getReason());
		}
		return Constant.COM_RESP;
	}

	/**
	 * 添加公告
	 */
	@ApiOperation("添加圈子公告")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "circleid", value = "圈子id", required = true, paramType = "query", dataType = "string"),
			@ApiImplicitParam(name = "title", value = "公告标题", paramType = "query", dataType = "string"),
			@ApiImplicitParam(name = "content", value = "公告内容", paramType = "query", dataType = "string"),
			@ApiImplicitParam(name = "images", value = "公告图片数组", dataType = "file") })
	@PostMapping("/add-notice")
	public String ciecleAddNotice(@RequestParam String circleid, String title, String content, MultipartFile[] images,
			HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		String userid = userBo.getId();
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(), ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		if (circleBo.getCreateuid().equals(userid) || circleBo.getMasters().contains(userid)) {
			CircleNoticeBo noticeBo = new CircleNoticeBo();
			noticeBo.setContent(content);
			noticeBo.setTitle(title);
			noticeBo.setCreateuid(userid);
			noticeBo.setCircleid(circleid);
			noticeBo.setNoticeType(0);
			// 发布人默认阅读
			LinkedHashSet<String> readUsers = noticeBo.getReadUsers();
			readUsers.add(userid);
			HashSet<String> users = circleBo.getUsers();
			users.remove(userid);
			LinkedHashSet<String> unReadUsers = new LinkedHashSet<>();
			unReadUsers.addAll(users);
			noticeBo.setUnReadUsers(unReadUsers);
			noticeBo.setType(0);
			if (images != null) {
				LinkedHashSet<String> files = noticeBo.getImages();
				for (MultipartFile file : images) {
					long time = Calendar.getInstance().getTimeInMillis();
					String fileName = String.format("%s-%d-%s", userid, time, file.getOriginalFilename());
					String path = CommonUtil.upload(file, Constant.CIRCLE_PICTURE_PATH, fileName, 0);
					logger.info("circle add notice pic path: {},  size: {} ", path, file.getSize());
					files.add(path);
				}
				noticeBo.setImages(files);
			}
			circleService.addNotice(noticeBo);
		} else {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_MASTER_NULL.getIndex(),
					ERRORCODE.CIRCLE_MASTER_NULL.getReason());
		}
		return Constant.COM_RESP;
	}

	/**
	 * 添加或修改公告
	 */
	@ApiOperation("修改圈子公告")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "noticeid", value = "公告id", required = true, paramType = "query", dataType = "string"),
			@ApiImplicitParam(name = "title", value = "公告标题", paramType = "query", dataType = "string"),
			@ApiImplicitParam(name = "content", value = "公告内容", paramType = "query", dataType = "string"),
			@ApiImplicitParam(name = "addImages", value = "新增的公告图片", dataType = "file"),
			@ApiImplicitParam(name = "delImages", value = "要删除的公告图片url，多个以逗号隔开", paramType = "query", dataType = "string") })
	@PostMapping("/update-notice")
	public String ciecleNotice(@RequestParam String noticeid, String title, String content, MultipartFile[] addImages,
			String delImages, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		String userid = userBo.getId();
		CircleNoticeBo noticeBo = circleService.findNoticeById(noticeid);
		if (noticeBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_NOTICE_NULL.getIndex(),
					ERRORCODE.CIRCLE_NOTICE_NULL.getReason());
		}
		CircleBo circleBo = circleService.selectById(noticeBo.getCircleid());
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(), ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		if (!circleBo.getCreateuid().equals(userid) && !circleBo.getMasters().contains(userid)) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_MASTER_NULL.getIndex(),
					ERRORCODE.CIRCLE_MASTER_NULL.getReason());
		}
		if (StringUtils.isNotEmpty(content)) {
			noticeBo.setContent(content);
		}
		if (StringUtils.isNotEmpty(title)) {
			noticeBo.setTitle(title);
		}
		noticeBo.setUpdateuid(userid);
		noticeBo.setUpdateTime(new Date());
		noticeBo.setType(1);
		LinkedHashSet<String> files = noticeBo.getImages();
		if (addImages != null) {
			for (MultipartFile file : addImages) {
				long time = Calendar.getInstance().getTimeInMillis();
				String fileName = String.format("%s-%d-%s", userid, time, file.getOriginalFilename());
				String path = CommonUtil.upload(file, Constant.CIRCLE_PICTURE_PATH, fileName, 0);
				files.add(path);
			}
			noticeBo.setImages(files);
		}
		if (StringUtils.isNotEmpty(delImages)) {
			String[] urls = CommonUtil.getIds(delImages);
			for (String url : urls) {
				files.remove(url);
			}
		}
		circleService.updateNotice(noticeBo);
		return Constant.COM_RESP;
	}

	/**
	 * 添加或修改公告
	 */
	@ApiOperation("获取圈子公告详情")
	@PostMapping("/get-notice")
	public String getNotice(@RequestParam String noticeid, HttpServletRequest request, HttpServletResponse response) {
		CircleNoticeBo noticeBo = circleService.findNoticeById(noticeid);
		if (noticeBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_NOTICE_NULL.getIndex(),
					ERRORCODE.CIRCLE_NOTICE_NULL.getReason());
		}
		CircleBo circleBo = circleService.selectById(noticeBo.getCircleid());
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(), ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		UserBo loginUser = getUserLogin(request);
		LinkedHashSet<String> readUsers = noticeBo.getReadUsers();
		int readNum = readUsers.size();
		int role = 0;
		if (loginUser != null) {
			String userid = loginUser.getId();
			HashSet<String> users = circleBo.getUsers();
			asyncController.updateNoticeRead(users, noticeid, loginUser.getId());
			readNum = !readUsers.contains(userid) && users.contains(userid) ? readNum + 1 : readNum;
			role = getUserCircleRole(circleBo, userid);
		}
		UserBo userBo = userService.getUser(noticeBo.getCreateuid());
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("ret", 0);
		map.put("noticeid", noticeBo.getId());
		map.put("noticeTitle", noticeBo.getTitle());
		map.put("notice", noticeBo.getContent());
		map.put("noticeTime", noticeBo.getCreateTime());
		map.put("readNum", readNum);
		map.put("image", noticeBo.getImages());
		if (userBo != null) {
			UserBaseVo userBaseVo = new UserBaseVo();
			BeanUtils.copyProperties(userBo, userBaseVo);
			userBaseVo.setRole(getUserCircleRole(circleBo, userBo.getId()));
			map.put("noticeUser", userBaseVo);
		}
		map.put("userRole", role);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 添加或修改公告
	 */
	@ApiOperation("圈子公告阅读详情")
	@PostMapping("/notice-read")
	public String getNoticeRead(@RequestParam String noticeid, HttpServletRequest request,
			HttpServletResponse response) {
		CircleNoticeBo noticeBo = circleService.findNoticeById(noticeid);
		if (noticeBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_NOTICE_NULL.getIndex(),
					ERRORCODE.CIRCLE_NOTICE_NULL.getReason());
		}
		LinkedHashSet<String> readUsers = noticeBo.getReadUsers();
		LinkedHashSet<String> unReadUser = noticeBo.getUnReadUsers();

		List<String> readids = new LinkedList<>(readUsers);
		List<String> unReadids = new LinkedList<>(unReadUser);

		List<UserBaseVo> readVos = new LinkedList<>();
		List<UserBo> readBos = userService.findUserByIds(readids);
		for (UserBo userBo : readBos) {
			UserBaseVo baseVo = new UserBaseVo();
			BeanUtils.copyProperties(userBo, baseVo);
			readVos.add(baseVo);
		}

		List<UserBaseVo> unReadVos = new LinkedList<>();
		List<UserBo> unReadBos = userService.findUserByIds(unReadids);
		for (UserBo userBo : unReadBos) {
			UserBaseVo baseVo = new UserBaseVo();
			BeanUtils.copyProperties(userBo, baseVo);
			unReadVos.add(baseVo);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("readNum", readUsers.size());
		map.put("unReadNum", unReadUser.size());
		map.put("readUserVos", readVos);
		map.put("unReadUserVos", unReadVos);
		return JSONObject.fromObject(map).toString();
	}

	@ApiOperation("删除圈子公告")
	@PostMapping("/delete-notice")
	public String deleteNoticeRead(@RequestParam String noticeid, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		CircleNoticeBo noticeBo = circleService.findNoticeById(noticeid);
		if (noticeBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_NOTICE_NULL.getIndex(),
					ERRORCODE.CIRCLE_NOTICE_NULL.getReason());
		}
		String userid = userBo.getId();
		CircleBo circleBo = circleService.selectById(noticeBo.getCircleid());
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(), ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		if (circleBo.getCreateuid().equals(userid) || circleBo.getMasters().contains(userid)) {
			circleService.deleteNotice(noticeid, userid);
		} else {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_MASTER_NULL.getIndex(),
					ERRORCODE.CIRCLE_MASTER_NULL.getReason());
		}
		return Constant.COM_RESP;
	}

	@ApiOperation("获取圈子公告历史列表, 返回最近10条")
	@PostMapping("/get-notice-list")
	public String getNoticeList(@RequestParam String circleid, int page, int limit, HttpServletRequest request,
			HttpServletResponse response) {
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(), ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		List<CircleNoticeBo> noticeBos = circleService.findCircleNotice(circleid, 0, page, limit);
		JSONArray array = new JSONArray();
		if (!CommonUtil.isEmpty(noticeBos)) {
			for (CircleNoticeBo noticeBo : noticeBos) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("noticeid", noticeBo.getId());
				jsonObject.put("noticeTitle", noticeBo.getTitle());
				jsonObject.put("notice", noticeBo.getContent());
				jsonObject.put("noticeTime", noticeBo.getCreateTime());
				jsonObject.put("image", noticeBo.getImages());
				jsonObject.put("readNum", noticeBo.getReadUsers().size());
				UserBo userBo = userService.getUser(noticeBo.getCreateuid());
				if (userBo != null) {
					UserBaseVo userBaseVo = new UserBaseVo();
					BeanUtils.copyProperties(userBo, userBaseVo);
					int role = getUserCircleRole(circleBo, userBo.getId());
					userBaseVo.setRole(role);
					jsonObject.put("noticeUser", userBaseVo);
				}
				array.add(jsonObject);
			}
		}
		map.put("noticeList", array);
		UserBo loginUser = getUserLogin(request);
		if (loginUser != null) {
			map.put("userRole", getUserCircleRole(circleBo, loginUser.getId()));
		} else {
			map.put("userRole", 0);
		}
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 添加或修改公告
	 */
	@ApiOperation("获取所有未读公告信息")
	@PostMapping("/unRead-notice-list")
	public String unReadNotices(String circleid, int page, int limit, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		List<CircleNoticeBo> noticeBos = circleService.findUnReadNotices(userBo.getId(), circleid, 0, page, limit);
		JSONArray array = new JSONArray();
		if (!CommonUtil.isEmpty(noticeBos)) {
			for (CircleNoticeBo noticeBo : noticeBos) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("noticeid", noticeBo.getId());
				jsonObject.put("noticeTitle", noticeBo.getTitle());
				jsonObject.put("notice", noticeBo.getContent());
				jsonObject.put("noticeTime", noticeBo.getCreateTime());
				jsonObject.put("image", noticeBo.getImages());
				jsonObject.put("readNum", noticeBo.getReadUsers().size());
				UserBo user = userService.getUser(noticeBo.getCreateuid());
				if (userBo != null) {
					UserBaseVo userBaseVo = new UserBaseVo();
					BeanUtils.copyProperties(user, userBaseVo);
					jsonObject.put("noticeUser", userBaseVo);
				}
				array.add(jsonObject);
			}
		}
		map.put("noticeList", array);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 添加或修改公告
	 */
	@ApiOperation("将指定的公告集合设置为已读")
	@ApiImplicitParam(name = "noticeids", value = "公告id,多个以逗号隔开", required = true, paramType = "query", dataType = "string")
	@PostMapping("/update-unRead-list")
	public String readNotices(@RequestParam String noticeids, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		String[] ids = CommonUtil.getIds(noticeids);
		List<CircleNoticeBo> noticeBos = circleService.findNoticeByIds(ids);
		for (CircleNoticeBo noticeBo : noticeBos) {
			asyncController.updateNoticeRead(noticeBo.getId(), userBo.getId());
		}
		return Constant.COM_RESP;
	}

	/**
	 * 获取置顶用户在当前圈子的角色
	 * 
	 * @param circleBo
	 * @param userid
	 * @return
	 */
	private int getUserCircleRole(CircleBo circleBo, String userid) {
		if (circleBo.getCreateuid().equals(userid)) {
			return 2;
		} else if (circleBo.getMasters().contains(userid)) {
			return 1;
		}
		return 0;
	}

	/**
	 * 举报圈子
	 */
	@ApiOperation("举报圈子")
	@PostMapping("/feed-tips")
	public String feedTips(@RequestParam String circleid, @RequestParam String title, @RequestParam String content,
			@RequestParam(required = false) String contact, @RequestParam(required = false) MultipartFile[] images,
			HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(), ERRORCODE.CIRCLE_IS_NULL.getReason());
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
				String fileName = userBo.getId() + "-" + time + "-" + file.getOriginalFilename();
				logger.info(fileName);
				String path = CommonUtil.upload(file, Constant.FEEDBACK_PICTURE_PATH, fileName, 0);
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
	@ApiOperation("获取热门搜索关键词")
	@PostMapping("/hot-searchs")
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
	@ApiOperation("根据具体的省市区获取圈子")
	@PostMapping("/search-city")
	public String searchCitys(String province, String city, String district, int page, int limit,
			HttpServletRequest request, HttpServletResponse response) {

		if (StringUtils.isNotEmpty(city)) {
			seveKeys(city);
		}
		if (StringUtils.isNotEmpty(district)) {
			seveKeys(district);
		}
		List<CircleBo> circleBos = circleService.findByCitys(province, city, district, page, limit);
		UserBo userBo = getUserLogin(request);
		return bo2vos(circleBos, userBo);
	}

	/**
	 * 根据省市区搜索相关圈子
	 */
	@ApiOperation("根据省或者市或者区搜索相关圈子")
	@ApiImplicitParam(name = "city", value = "省市区名称", required = true, paramType = "query", dataType = "string")
	@PostMapping("/search-by-city")
	public String searchByCitys(String city, int page, int limit, HttpServletRequest request,
			HttpServletResponse response) {
		if (StringUtils.isNotEmpty(city)) {
			seveKeys(city);
		}
		List<CircleBo> circleBos = circleService.findByCityName(city, page, limit);
		UserBo userBo = getUserLogin(request);
		return bo2vos(circleBos, userBo);
	}

	private void seveKeys(String value) {
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
	@ApiOperation("热门城市搜索")
	@PostMapping("/hot-citys")
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
	@ApiOperation("相关圈子列表")
	@PostMapping("/related")
	public String relatedCircle(String circleid, int page, int limit, HttpServletRequest request,
			HttpServletResponse response) {

		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(), ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		List<CircleBo> circleBos = circleService.findRelatedCircles(circleid, circleBo.getTag(), circleBo.getSub_tag(),
				page, limit);
		if (circleBos == null || circleBos.isEmpty()) {
			circleBos = circleService.findRelatedCircles(circleid, circleBo.getTag(), "", page, limit);
		}
		return bo2vos(circleBos, getUserLogin(request));
	}

	/**
	 * 邀请加入圈子
	 * 
	 * @param circleid
	 * @param userids
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation("邀请好友加入圈子")
	@PostMapping("/invite-user")
	public String inviteUsers(@RequestParam String circleid, @RequestParam String userids, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(), ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		String userid = userBo.getId();
		String[] useridArr = CommonUtil.getIds(userids);
		if ((circleBo.getUsers().size() + useridArr.length) > 500) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_USER_MAX.getIndex(),
					ERRORCODE.CIRCLE_USER_MAX.getReason());
		}
		String path = "/circle/circle-info.do?circleid=" + circleid;
		String content = String.format("“%s”邀请您加入圈子【%s】，快去看看吧", userBo.getUserName(), circleBo.getName());
		updateHistory(userid, circleid, locationService, circleService);
		if (circleBo.getCreateuid().equals(userid) || circleBo.getMasters().contains(userid)) {
			for (String inviteId : useridArr) {
				ReasonBo reasonBo = reasonService.findByUserAndCircle(inviteId, circleid, Constant.ADD_APPLY);
				if (reasonBo == null) {
					reasonBo = new ReasonBo();
					reasonBo.setCircleid(circleid);
					reasonBo.setCreateuid(inviteId);
					reasonBo.setMasterApply(true);
					reasonBo.setReasonType(0);
					reasonBo.setStatus(Constant.ADD_APPLY);
					reasonService.insert(reasonBo);
				} else {
					reasonService.updateMasterApply(reasonBo.getId(), Constant.ADD_APPLY, true);
				}
			}
			JPushUtil.push(titlePush, content, path, useridArr);
		} else {
			JPushUtil.push(titlePush, content, path, useridArr);
		}
		addMessage(messageService, path, content, titlePush, userid, useridArr);
		return Constant.COM_RESP;
	}

	/**
	 * 邀请好友列表
	 * 
	 * @param circleid
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation("邀请好友列表")
	@PostMapping("/invite-friend-list")
	public String inviteList(String circleid, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		List<FriendsVo> voList = new ArrayList<>();
		List<FriendsBo> friendsBos = friendsService.getFriendByUserid(userBo.getId());
		if (StringUtils.isEmpty(circleid)) {
			for (FriendsBo friendsBo : friendsBos) {
				FriendsVo vo = new FriendsVo();
				BeanUtils.copyProperties(friendsBo, vo);
				String friendid = friendsBo.getFriendid();
				UserBo friend = userService.getUser(friendsBo.getFriendid());
				if (friend == null) {
					continue;
				}
				vo.setSex(friend.getSex());
				vo.setUsername(friend.getUserName());
				vo.setPicture(friend.getHeadPictureName());
				vo.setBackname(friendsBo.getBackname());
				vo.setUserid("");
				voList.add(vo);
			}
		} else {
			CircleBo circleBo = circleService.selectById(circleid);
			if (circleBo == null) {
				return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(),
						ERRORCODE.CIRCLE_IS_NULL.getReason());
			}
			HashSet<String> circleUsers = circleBo.getUsers();
			for (FriendsBo friendsBo : friendsBos) {
				FriendsVo vo = new FriendsVo();
				BeanUtils.copyProperties(friendsBo, vo);
				String friendid = friendsBo.getFriendid();
				if (circleUsers.contains(friendid)) {
					continue;
				}
				UserBo friend = userService.getUser(friendsBo.getFriendid());
				if (friend == null) {
					continue;
				}
				vo.setSex(friend.getSex());
				vo.setUsername(friend.getUserName());
				vo.setPicture(friend.getHeadPictureName());
				vo.setBackname(friendsBo.getBackname());
				vo.setUserid("");
				voList.add(vo);
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("userVos", voList);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 圈子好友搜索
	 * 
	 * @param circleid
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation("圈子内好友搜索")
	@PostMapping("/user-search")
	public String userSearch(String circleid, String keyword, HttpServletRequest request,
			HttpServletResponse response) {

		UserBo userBo = getUserLogin(request);

		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(), ERRORCODE.CIRCLE_IS_NULL.getReason());
		}

		List<String> userHas = new ArrayList<>();
		List<UserBo> userBos = userService.searchCircleUsers(circleBo.getUsers(), keyword);
		HashSet<String> masters = circleBo.getMasters();
		List<UserBaseVo> userList = new ArrayList<>();
		for (UserBo user : userBos) {
			String userid = user.getId();
			if (userBo != null && userBo.getId().equals(userid)) {
				continue;
			}
			UserCircleVo userBaseVo = circleUser2Vo(user, circleBo.getCreateuid(), masters);
			long circleNum = circleService.findCreateCricles(userid);

			userBaseVo.setHasCircleNum((int) circleNum);
			userBaseVo.setMaxCircleNum(user.getLevel() * 5);
			userList.add(userBaseVo);
			userHas.add(userid);
		}
		// 登录后可以查询好友信息的
		if (userBo != null) {
			List<FriendsBo> friendsBos = friendsService.searchCircleUsers(circleBo.getUsers(), userBo.getId(), keyword);
			for (FriendsBo friend : friendsBos) {
				String friendid = friend.getFriendid();
				if (userHas.contains(friendid)) {
					continue;
				}
				UserBo user = userService.getUser(friendid);
				if (user == null) {
					continue;
				}
				UserCircleVo userBaseVo = circleUser2Vo(user, circleBo.getCreateuid(), masters);
				long circleNum = circleService.findCreateCricles(friendid);
				userBaseVo.setHasCircleNum((int) circleNum);
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
	 * 
	 * @param circleid
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation("邀请好友搜索")
	@PostMapping("/invite-user-search")
	public String inviteUserSearch(String circleid, String keyword, HttpServletRequest request,
			HttpServletResponse response) {

		UserBo userBo = getUserLogin(request);
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(), ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		HashSet<String> masters = circleBo.getMasters();
		List<UserBaseVo> userList = new ArrayList<>();
		// 登录后可以查询好友信息的
		if (userBo != null) {
			List<FriendsBo> friendsBos = friendsService.searchInviteCircleUsers(circleBo.getUsers(), userBo.getId(),
					keyword);
			for (FriendsBo friend : friendsBos) {
				String friendid = friend.getFriendid();
				UserBo user = userService.getUser(friendid);
				if (user == null) {
					continue;
				}
				UserCircleVo userBaseVo = circleUser2Vo(user, circleBo.getCreateuid(), masters);
				long circleNum = circleService.findCreateCricles(friendid);
				userBaseVo.setHasCircleNum((int) circleNum);
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
	 * 
	 * @param circleid
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation("加入圈子")
	@PostMapping("/add-in-circle")
	public String applyAddInsert(String circleid, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(), ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		HashSet<String> users = circleBo.getUsers();
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
			// 如果不需要验证
			users.add(userBo.getId());
			circleAddUsers(circleid, users);
			addReason(userBo.getId(), circleid);
			isAdd = true;
		} else {
			ReasonBo reasonBo = reasonService.findByUserAdd(userBo.getId(), circleid);
			if (reasonBo != null) {
				// 如果有申请记录
				if (reasonBo.getStatus() == Constant.ADD_APPLY) {
					// 状态，0 表示申请； 1 表示通过， -1表示拒绝 Constant.ADD_APPLY=0
					isAdd = reasonBo.isMasterApply();
					if (isAdd) {
						// 如果是管理员邀请则直接通过
						users.add(userBo.getId());
						circleAddUsers(circleid, users);
						reasonService.updateApply(reasonBo.getId(), Constant.ADD_AGREE, "");
					}
				} else if (reasonBo.getStatus() == Constant.ADD_AGREE) {
					// 如果已经同意
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
	@ApiOperation("转发圈子到我的动态")
	@PostMapping("/forward-dynamic")
	public String forwardDynamic(String circleid, String view, String landmark, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(), ERRORCODE.CIRCLE_IS_NULL.getReason());
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
		dynamicBo.setLandmark(landmark);
		dynamicBo.setType(Constant.CIRCLE_TYPE);
		dynamicBo.setSourceName(circleBo.getName());
		dynamicService.addDynamic(dynamicBo);
		circleService.updateCircleHot(circleid, 1, Constant.CIRCLE_TRANS);
		updateDynamicNums(userBo.getId(), 1, dynamicService, redisServer);
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("dynamicid", dynamicBo.getId());
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 收藏帖子
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation("收藏圈子")
	@PostMapping("/collect-circle")
	public String colNotes(String circleid, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.CIRCLE_IS_NULL.getIndex(), ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		CollectBo chatBo = collectService.findByUseridAndTargetid(userBo.getId(), circleid);
		if (chatBo != null) {
			return CommonUtil.toErrorResult(ERRORCODE.COLLECT_EXIST.getIndex(), ERRORCODE.COLLECT_EXIST.getReason());
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
		circleService.updateCircleHot(circleid, 1, Constant.CIRCLE_TRANS);
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("col-time", CommonUtil.time2str(chatBo.getCreateTime()));
		return JSONObject.fromObject(map).toString();
	}

	@ApiOperation("热门圈子")
	@PostMapping("/hot-circles")
	public String hotCircles(String city, int page, int limit, HttpServletRequest request,
			HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		List<CircleBo> circleBoList = circleService.findHotCircles(city, page, limit);
		return bo2vos(circleBoList, userBo);
	}

	private void addReason(String userid, String circleid) {
		ReasonBo reasonBo = reasonService.findByUserAdd(userid, circleid);
		if (reasonBo == null) {
			reasonBo = new ReasonBo();
			reasonBo.setCircleid(circleid);
			reasonBo.setCreateuid(userid);
			reasonBo.setStatus(1);
			reasonBo.setAddType(0);
			reasonBo.setReasonType(0);
			reasonService.insert(reasonBo);
		} else if (reasonBo.getStatus() != 1) {
			reasonService.updateApply(reasonBo.getId(), 1, "");
		}
	}

	private UserCircleVo circleUser2Vo(UserBo userBo, String createuid, HashSet<String> masters) {
		UserCircleVo userBaseVo = new UserCircleVo();
		BeanUtils.copyProperties(userBo, userBaseVo);
		if (createuid.equals(userBo.getId())) {
			userBaseVo.setRole(2);
		} else if (masters.contains(userBo.getId())) {
			userBaseVo.setRole(1);
		} else {
			userBaseVo.setRole(0);
		}
		return userBaseVo;
	}

	/**
	 * 红人列表实体类转换
	 * 
	 * @param redstarBos
	 * @return
	 */
	private List<UserStarVo> getStar(List<RedstarBo> redstarBos) {
		List<UserStarVo> userStarVos = new ArrayList<>();
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
		return userStarVos;
	}

	/**
	 * 圈子历史
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation("圈子操作历史详情")
	@PostMapping("/get-circle-his")
	public String getCircleHis(String historyid, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		CircleHistoryBo hisBo = circleService.findCircleHisById(historyid);
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("ret", 0);
		if (hisBo != null) {
			map.put("historyid", hisBo.getId());
			map.put("hisType", hisBo.getType());
			UserBo user = userService.getUser(hisBo.getUserid());
			UserBaseVo baseVo = new UserBaseVo();
			if (user != null) {
				BeanUtils.copyProperties(user, baseVo);
			}
			if (hisBo.getType() == 0) {
				map.put("userVo", baseVo);
				map.put("visitTime", hisBo.getCreateTime());
			} else if (hisBo.getType() == 1) {
				CircleBo circleBo = circleService.selectById(hisBo.getCircleid());
				CircleHeadVo headVo = new CircleHeadVo();
				if (circleBo != null) {
					headVo.setCircleid(circleBo.getId());
					headVo.setName(circleBo.getName());
					headVo.setHeadPicture(circleBo.getHeadPicture());
				}
				UserBo operate = userService.getUser(hisBo.getOperateid());
				UserBaseVo opUser = new UserBaseVo();
				if (operate != null) {
					BeanUtils.copyProperties(operate, opUser);
					opUser.setSex(operate.getSex());
				}
				map.put("title", hisBo.getTitle());
				map.put("content", hisBo.getContent());
				map.put("userVo", baseVo);
				map.put("operateUserVo", opUser);
				map.put("circleVo", headVo);
				map.put("operateTime", hisBo.getCreateTime());
			}
		}
		return JSON.toJSONString(map);
	}

	/**
	 * 我所有圈子的操作历史
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation("我所有圈子的操作历史")
	@PostMapping("/get-my-his")
	public String getMyCircleHis(int page, int limit, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		List<CircleHistoryBo> historyBos = circleService.findCircleHisByUserid(userBo.getId(), 1, page, limit);
		List<CircleHisVo> hisVos = new LinkedList<>();
		for (CircleHistoryBo historyBo : historyBos) {
			CircleHisVo hisVo = new CircleHisVo();
			hisVo.setHistoryid(historyBo.getId());
			hisVo.setContent(historyBo.getContent());
			hisVo.setTitle(historyBo.getTitle());
			hisVo.setOperateTime(historyBo.getCreateTime());
			hisVos.add(hisVo);
		}
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("ret", 0);
		map.put("historyVos", hisVos);
		return JSON.toJSONString(map);
	}

	private CircleHistoryBo addCircleOperateHis(String userid, String circleid, String opeateid, String title,
			String content) {
		CircleHistoryBo historyBo = new CircleHistoryBo();
		historyBo.setCircleid(circleid);
		historyBo.setUserid(userid);
		historyBo.setOperateid(opeateid);
		historyBo.setTitle(title);
		historyBo.setContent(content);
		historyBo.setType(1);
		circleService.insertHistory(historyBo);
		return historyBo;
	}

	/**
	 * 我所有圈子的操作历史
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation("圈子最新动态信息")
	@PostMapping("/new-situation")
	public String circleNews(String circleid, int page, int limit, HttpServletRequest request,
			HttpServletResponse response) {
		List<NoteBo> noteBos = noteService.finyByCreateTime(circleid, page, limit);
		List<NoteVo> noteVos = new LinkedList<>();
		UserBo loginUser = getUserLogin(request);
		String loginUserid = loginUser == null ? "" : loginUser.getId();
		for (NoteBo noteBo : noteBos) {
			NoteVo noteVo = new NoteVo();
			if (noteBo.getCreateuid().equals(loginUserid)) {
				boToVo(noteBo, noteVo, loginUser, loginUserid);
			} else {
				UserBo userBo = userService.getUser(noteBo.getCreateuid());
				boToVo(noteBo, noteVo, userBo, loginUserid);
			}
			ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(noteBo.getId(), loginUserid);
			noteVo.setMyThumbsup(thumbsupBo != null);
			noteVos.add(noteVo);
		}
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("ret", 0);
		if (loginUser != null) {
			List<MessageBo> messageBos = messageService.findUnReadByMyUserid(loginUserid, circleid);
			int num = 0;
			if (!CommonUtil.isEmpty(messageBos)) {
				num = messageBos.size();
				// 返回第一条动态评论人的
				MessageBo first = messageBos.get(0);
				UserBo user = userService.getUser(first.getCreateuid());
				if (user == null) {
					user = userService.getUser(messageBos.get(1).getCreateuid());
				}
				UserBaseVo baseVo = new UserBaseVo();
				BeanUtils.copyProperties(user, baseVo);
				map.put("noteDynamicFirstUser", baseVo);
			}
			map.put("noteDynamicNum", num);
		}
		map.put("noteVoList", noteVos);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 我所有圈子的操作历史
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation("当前圈子中帖子点赞或评论的最新动态信息")
	@PostMapping("/note-dynamic")
	public String noteDynamic(String circleid, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		String userid = userBo.getId();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("ret", 0);
		List<MessageBo> messageBos = messageService.findUnReadByMyUserid(userid, circleid);
		List<UserShowVo> userShowVos = new LinkedList<>();
		List<String> readids = new LinkedList<>();
		Map<String, NoteBo> noteMap = new HashMap<>();
		if (!CommonUtil.isEmpty(messageBos)) {
			messageBos.forEach(messageBo -> {
				readids.add(messageBo.getId());
				UserShowVo showVo = new UserShowVo();
				int type = messageBo.getType();
				String noteid = messageBo.getTargetid();
				NoteBo noteBo = null;
				if (noteMap.containsKey(noteid)) {
					noteBo = noteMap.get(noteid);
				} else {
					noteBo = noteService.selectById(noteid);
					if (noteBo == null) {
						return;
					}
					noteMap.put(noteid, noteBo);
				}
				showVo.setNoteid(noteid);
				if (!CommonUtil.isEmpty(noteBo.getPhotos())) {
					if ("video".equals(noteBo.getType())) {
						showVo.setNotePic(noteBo.getVideoPic());
						showVo.setNoteVideo(noteBo.getPhotos().get(0));
					} else {
						showVo.setNotePic(noteBo.getPhotos().get(0));
					}
				} else {
					showVo.setNoteContent(noteBo.getContent());
				}
				String creatuid = "";
				if (type == 1) {
					CommentBo commentBo = commentService.findById(messageBo.getSourceid());
					if (commentBo == null) {
						return;
					}
					showVo.setComment(commentBo.getContent());
					showVo.setThumbsup(false);
					showVo.setCreateTime(commentBo.getCreateTime());
					creatuid = commentBo.getCreateuid();
				} else if (type == 2) {
					ThumbsupBo thumbsupBo = thumbsupService.selectById(messageBo.getSourceid());
					if (thumbsupBo == null) {
						return;
					}
					showVo.setThumbsup(true);
					showVo.setCreateTime(thumbsupBo.getCreateTime());
					creatuid = thumbsupBo.getCreateuid();
				} else {
					return;
				}

				if (userid.equals(messageBo.getCreateuid())) {
					showVo.setId(userid);
					showVo.setUserName(userBo.getUserName());
					showVo.setBirthDay(userBo.getBirthDay());
					showVo.setHeadPictureName(userBo.getHeadPictureName());
					showVo.setSex(userBo.getSex());
				} else {
					UserBo user = userService.getUser(creatuid);
					if (user == null) {
						return;
					}
					showVo.setId(creatuid);
					showVo.setUserName(user.getUserName());
					showVo.setBirthDay(user.getBirthDay());
					showVo.setHeadPictureName(user.getHeadPictureName());
					showVo.setSex(user.getSex());
					FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorIdAgree(userid, user.getId());
					if (friendsBo != null && !org.springframework.util.StringUtils.isEmpty(friendsBo.getBackname())) {
						showVo.setUserName(friendsBo.getBackname());
					}
				}
				userShowVos.add(showVo);
			});
		}

		if (readids.size() > 0) {
			messageService.betchUpdateMessage(readids, 1);
		}
		map.put("userVoList", userShowVos);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 清空圈子内帖子动态信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation("清空圈子内帖子动态信息")
	@PostMapping("/clear-note-dynamic")
	public String clearNoteDynamic(String circleid, HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		messageService.clearUnReadByMyUserid(userBo.getId(), circleid);
		return Constant.COM_RESP;
	}

	private void addValues(PartyListVo listVo, PartyBo partyBo) {
		LinkedHashSet<String> startTimes = partyBo.getStartTime();
		BeanUtils.copyProperties(partyBo, listVo);
		if (partyBo.getStatus() != 3) {
			int status = getPartyStatus(startTimes, partyBo.getAppointment());
			// 人数以报满
			if (status == 1 && partyBo.getUserLimit() <= partyBo.getPartyUserNum() && partyBo.getUserLimit() != 0) {
				if (partyBo.getStatus() != 2) {
					updatePartyStatus(partyBo.getId(), 2);
					listVo.setStatus(2);
				}
			} else if (status != partyBo.getStatus()) {
				listVo.setStatus(status);
				updatePartyStatus(partyBo.getId(), status);
			}
		}
		listVo.setPartyid(partyBo.getId());
		listVo.setUserNum(partyBo.getPartyUserNum());
	}

	/**
	 * 实体类转换
	 * 
	 * @param circleBos
	 * @return
	 */
	private String bo2vos(List<CircleBo> circleBos, UserBo userBo) {
		List<CircleVo> listVo = new LinkedList<CircleVo>();
		for (CircleBo item : circleBos) {
			listVo.add(bo2vo(item, userBo, 0));
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("circleVoList", listVo);
		return JSONObject.fromObject(map).toString();
	}

	private CircleVo bo2vo(CircleBo circleBo, UserBo userBo, int top) {
		CircleVo circleVo = new CircleVo();
		BeanUtils.copyProperties(circleBo, circleVo);
		circleVo.setId(circleBo.getId());
		circleVo.setName(circleBo.getName());
		long noteSize = noteService.findNotesNum(circleBo.getId());
		circleVo.setNotesSize(noteSize);
		circleVo.setUsersSize(circleBo.getUsers().size());
		circleVo.setTop(top);
		if (null != userBo) {
			ReasonBo reasonBo = reasonService.findByUserAndCircle(userBo.getId(), circleBo.getId(), Constant.ADD_AGREE);
			// 圈子未读数量信息
			if (reasonBo != null) {
				circleVo.setUnReadNum(reasonBo.getUnReadNum());
			}
			// 查找圈子加入历史
			CircleAddBo addBo = circleService.findHisByUserAndCircle(userBo.getId(), circleBo.getId());
			if (null != addBo) {
				circleVo.setUserAdd(addBo.getStatus());
			}
		}
		return circleVo;
	}

	/**
	 * 清零数据
	 * 
	 * @param userid
	 * @param circleid
	 */
	private void updateCircieUnReadZero(String userid, String circleid) {
		RLock lock = redisServer.getRLock(userid + "UnReadNumLock");
		try {
			lock.lock(2, TimeUnit.SECONDS);
			reasonService.updateUnReadNumZero(userid, circleid);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 加锁同意
	 * 
	 * @param cirlceid
	 * @param type
	 * @param users
	 * @param userApplys
	 * @param refuses
	 */
	private void circleAddUsers(String cirlceid, int type, HashSet<String> users, HashSet<String> userApplys,
			HashSet<String> refuses) {
		RLock lock = redisServer.getRLock(cirlceid + "add");
		try {
			lock.lock(3, TimeUnit.SECONDS);
			if (type == 0) {
				circleService.updateUsers(cirlceid, users);
			} else if (type == 1) {
				circleService.updateUsersApply(cirlceid, userApplys);
			} else if (type == 2) {
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
	 * 
	 * @param cirlceid
	 * @param users
	 */
	private void circleAddUsers(String cirlceid, HashSet<String> users) {
		this.circleAddUsers(cirlceid, 0, users, null, null);
	}

	/**
	 * 加锁同意
	 * 
	 * @param cirlceid
	 */
	private void circleAddUserApply(String cirlceid, HashSet<String> userApplys) {
		this.circleAddUsers(cirlceid, 1, null, userApplys, null);
	}

	/**
	 * 加锁同意
	 * 
	 * @param cirlceid
	 * @param users
	 */
	private void circleAddUsers(String cirlceid, HashSet<String> users, HashSet<String> userApplys) {
		this.circleAddUsers(cirlceid, 2, users, userApplys, null);
	}

	/**
	 * 加锁同意
	 * 
	 * @param cirlceid
	 */
	private void circleAddUsersRefuse(String cirlceid, HashSet<String> userApplys, HashSet<String> refuses) {
		this.circleAddUsers(cirlceid, 3, null, userApplys, refuses);
	}

	/**
	 *
	 * @param noteBo
	 * @param noteVo
	 * @param creatBo
	 * @param userid
	 */
	private void boToVo(NoteBo noteBo, NoteVo noteVo, UserBo creatBo, String userid) {
		BeanUtils.copyProperties(noteBo, noteVo);
		// 表示转发
		if (noteBo.getForward() == 1) {
			noteVo.setSourceid(noteBo.getSourceid());
			noteVo.setForward(true);
			// 0 表示转发的帖子，1 表示转发的资讯
			if (noteBo.getNoteType() == 1) {
				int inforType = noteBo.getInforType();
				noteVo.setInforType(inforType);
				noteVo.setForwardType(1);
				switch (inforType) {
				case Constant.INFOR_HEALTH:
					InforBo inforBo = inforService.findById(noteBo.getSourceid());
					if (inforBo != null) {
						noteVo.setPhotos(inforBo.getImageUrls());
						noteVo.setSubject(inforBo.getTitle());
						noteVo.setVisitCount((long) inforBo.getVisitNum());
						noteVo.setInforTypeName(inforBo.getModule());
					}
					break;
				case Constant.INFOR_SECRITY:
					SecurityBo securityBo = inforService.findSecurityById(noteBo.getSourceid());
					if (securityBo == null) {
						noteVo.setSubject(securityBo.getTitle());
						noteVo.setVisitCount((long) securityBo.getVisitNum());
						noteVo.setInforTypeName(securityBo.getNewsType());
					}
					break;
				case Constant.INFOR_RADIO:
					BroadcastBo broadcastBo = inforService.findBroadById(noteBo.getSourceid());
					if (broadcastBo == null) {
						noteVo.setSubject(broadcastBo.getTitle());
						noteVo.setInforUrl(broadcastBo.getBroadcast_url());
						noteVo.setVisitCount((long) broadcastBo.getVisitNum());
						noteVo.setInforTypeName(broadcastBo.getModule());
					}
					break;
				case Constant.INFOR_VIDEO:
					VideoBo videoBo = inforService.findVideoById(noteBo.getSourceid());
					if (videoBo == null) {
						noteVo.setSubject(videoBo.getTitle());
						noteVo.setInforUrl(videoBo.getUrl());
						noteVo.setVideoPic(videoBo.getPoster());
						noteVo.setVisitCount((long) videoBo.getVisitNum());
						noteVo.setInforTypeName(videoBo.getModule());
					}
					break;
				default:
					break;
				}
			} else {
				NoteBo sourceNote = noteService.selectById(noteBo.getSourceid());
				if (sourceNote != null) {
					noteVo.setSubject(sourceNote.getSubject());
					noteVo.setContent(sourceNote.getContent());
					noteVo.setPhotos(sourceNote.getPhotos());
					noteVo.setVideoPic(sourceNote.getVideoPic());
					addNoteAtUsers(sourceNote, noteVo, userid);
					UserBo from = userService.getUser(sourceNote.getCreateuid());
					if (from != null) {
						noteVo.setFromUserid(from.getId());
						if (!org.springframework.util.StringUtils.isEmpty(userid)) {
							FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorIdAgree(userid, from.getId());
							if (friendsBo != null) {
								noteVo.setFromUserName(friendsBo.getBackname());
							} else {
								noteVo.setFromUserName(from.getUserName());
							}
						} else {
							noteVo.setFromUserName(from.getUserName());
						}
						noteVo.setFromUserPic(from.getHeadPictureName());
						noteVo.setFromUserSex(from.getSex());
						noteVo.setFromUserSign(from.getPersonalizedSignature());
						noteVo.setFromUserBirth(from.getBirthDay());
						noteVo.setFromUserLevel(from.getLevel());
					}
				}
			}
		} else {
			addNoteAtUsers(noteBo, noteVo, userid);
		}
		if (creatBo != null) {
			if (!"".equals(userid) && !userid.equals(creatBo.getId())) {
				FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorIdAgree(userid, creatBo.getId());
				if (friendsBo != null && !org.springframework.util.StringUtils.isEmpty(friendsBo.getBackname())) {
					noteVo.setUsername(friendsBo.getBackname());
				} else {
					noteVo.setUsername(creatBo.getUserName());
				}
			} else {
				noteVo.setUsername(creatBo.getUserName());
			}
			noteVo.setUserLevel(creatBo.getLevel());
			noteVo.setSex(creatBo.getSex());
			noteVo.setBirthDay(creatBo.getBirthDay());
			noteVo.setHeadPictureName(creatBo.getHeadPictureName());
		}
		noteVo.setPosition(noteBo.getPosition());
		noteVo.setCommontCount(noteBo.getCommentcount());
		noteVo.setVisitCount(noteBo.getVisitcount());
		noteVo.setNodeid(noteBo.getId());
		noteVo.setTransCount(noteBo.getTranscount());
		noteVo.setThumpsubCount(noteBo.getThumpsubcount());
	}

	/**
	 * 获取帖子中@的用户信息
	 * 
	 * @param noteBo
	 * @param noteVo
	 * @param loginUserid
	 */
	private void addNoteAtUsers(NoteBo noteBo, NoteVo noteVo, String loginUserid) {
		LinkedList<String> atUsers = noteBo.getAtUsers();
		if (!CommonUtil.isEmpty(atUsers)) {
			List<UserNoteVo> atUserVos = new LinkedList<>();
			List<UserBo> userBos = userService.findUserByIds(atUsers);
			for (UserBo userBo : userBos) {
				UserNoteVo userNoteVo = new UserNoteVo();
				userNoteVo.setSex(userBo.getSex());
				userNoteVo.setUserid(userBo.getId());
				userNoteVo.setUserName(userBo.getUserName());
				if (org.springframework.util.StringUtils.isEmpty(loginUserid)) {
					FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorIdAgree(loginUserid, userBo.getId());
					if (friendsBo != null && !org.springframework.util.StringUtils.isEmpty(friendsBo.getBackname())) {
						userNoteVo.setBackName(friendsBo.getBackname());
					}
				}
				atUserVos.add(userNoteVo);
			}
			noteVo.setAtUsers(atUserVos);
		}
	}

	private void updatePartyStatus(String partyid, int status) {
		partyService.updatePartyStatus(partyid, status);
		// 聚会结束,删除所有临时聊天
		if (status == 3) {
			chatroomService.deleteTempChat(partyid, Constant.ROOM_SINGLE);
		}
	}
}
