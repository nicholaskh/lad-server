package com.lad.controller;

import com.lad.bo.*;
import com.lad.redis.RedisServer;
import com.lad.service.*;
import com.lad.util.*;
import com.lad.vo.CommentVo;
import com.lad.vo.NoteVo;
import com.lad.vo.UserBaseVo;
import com.lad.vo.UserThumbsupVo;
import com.mongodb.BasicDBObject;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("note")
public class NoteController extends BaseContorller {

	private final Logger logger = LogManager.getLogger(NoteController.class);

	@Autowired
	private INoteService noteService;
	@Autowired
	private ICircleService circleService;
	@Autowired
	private IUserService userService;

	@Autowired
	private ICommentService commentService;

	@Autowired
	private IThumbsupService thumbsupService;
	@Autowired
	private RedisServer redisServer;

	@Autowired
	private ILocationService locationService;

	@Autowired
	private IDynamicService dynamicService;

	@Autowired
	private IReasonService reasonService;

	@Autowired
	private IFriendsService friendsService;

	@Autowired
	private ICollectService collectService;


	private String pushTitle = "互动通知";


	@RequestMapping("/insert")
	@ResponseBody
	public String insert(String noteJson, MultipartFile[] pictures,
			HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		NoteBo noteBo = null;
		try {
			JSONObject jsonObject = JSONObject.fromObject(noteJson);
			noteBo = (NoteBo)JSONObject.toBean(jsonObject, NoteBo.class);
		} catch (Exception e) {
			return CommonUtil.toErrorResult(ERRORCODE.FORMAT_ERROR.getIndex(),
					ERRORCODE.FORMAT_ERROR.getReason());
		}

		String circleid = noteBo.getCircleId();
		updateHistory(userBo.getId(), circleid, locationService, circleService);
		noteBo.setVisitcount(1);
		noteBo.setCreateuid(userBo.getId());
		noteBo.setVisitcount(1);
		noteBo.setTemp(1);
		LinkedList<String> photos = new LinkedList<>();
		String userId =  userBo.getId();
		if (pictures != null) {
			for (MultipartFile file : pictures) {
				Long time = Calendar.getInstance().getTimeInMillis();
				String fileName = String.format("%s-%d-%s", userId, time, file.getOriginalFilename());
				logger.info(fileName);
				if ("video".equals(noteBo.getType())) {
					String[] paths = CommonUtil.uploadVedio(file, Constant.NOTE_PICTURE_PATH, fileName, 0);
					photos.add(paths[0]);
					noteBo.setVideoPic(paths[1]);
				} else {
					String path = CommonUtil.upload(file, Constant.NOTE_PICTURE_PATH,
							fileName, 0);
					logger.info(path);
					photos.add(path);
				}
			}
		}
		noteBo.setPhotos(photos);
		noteService.insert(noteBo);
		RLock lock = redisServer.getRLock(circleid + "noteSize");
		try {
			lock.lock(2,TimeUnit.SECONDS);
			circleService.updateNotes(circleid, 1);
		} finally {
			lock.unlock();
		}
		if (noteBo.isAsync()) {
			DynamicBo dynamicBo = new DynamicBo();
			dynamicBo.setTitle(noteBo.getSubject());
			dynamicBo.setView("我发表了帖子");
			dynamicBo.setCreateuid(userId);
			dynamicBo.setOwner(noteBo.getCreateuid());
			dynamicBo.setPhotos(new LinkedHashSet<>(noteBo.getPhotos()));
			dynamicBo.setType(Constant.NOTE_TYPE);

			CircleBo circleBo = circleService.selectById(noteBo.getCircleId());
			if (circleBo != null) {
				dynamicBo.setSourceName(circleBo.getName());
			}
			dynamicBo.setCreateuid(userBo.getId());
			dynamicService.addDynamic(dynamicBo);
		}
		userService.addUserLevel(userBo.getId(), 1, Constant.LEVEL_NOTE);
		updateCircleHot(circleService, redisServer, circleid, 1, Constant.CIRCLE_NOTE);
		updateCircleHot(circleService, redisServer, circleid, 1, Constant.CIRCLE_NOTE_VISIT);
		updateDynamicNums(userId, 1, dynamicService, redisServer);
		updateCircieNoteUnReadNum(userId, circleid);
		NoteVo noteVo = new NoteVo();
		boToVo(noteBo, noteVo, userBo, "");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("noteVo", noteVo);
		return JSONObject.fromObject(map).toString();
	}




	@RequestMapping("/photo")
	@ResponseBody
	public String note_picture(@RequestParam("photos") MultipartFile[] files,
			@RequestParam(required = true) String noteid,
			HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		String userId = userBo.getId();

		NoteBo noteBo = noteService.selectById(noteid);
		if (null == noteBo) {
			return CommonUtil.toErrorResult(ERRORCODE.NOTE_IS_NULL.getIndex(),
					ERRORCODE.NOTE_IS_NULL.getReason());
		}
		LinkedList<String> photos = noteBo.getPhotos();
		List<String> paths = new ArrayList<>();
		for (MultipartFile file : files) {
			Long time = Calendar.getInstance().getTimeInMillis();
			String fileName = String.format("%s-%d-%s", userId, time, file.getOriginalFilename());
			String path = CommonUtil.upload(file, Constant.NOTE_PICTURE_PATH,
					fileName, 0);
			photos.add(path);
			paths.add(path);
		}
		noteService.updatePhoto(noteid, photos);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("path", paths);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/thumbsup")
	@ResponseBody
	public String thumbsup(String noteid, HttpServletRequest request, HttpServletResponse response){
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		NoteBo noteBo = noteService.selectById(noteid);
		if (noteBo ==null) {
			return CommonUtil.toErrorResult(ERRORCODE.NOTE_IS_NULL.getIndex(),
					ERRORCODE.NOTE_IS_NULL.getReason());
		}
		ThumbsupBo thumbsupBo = thumbsupService.findHaveOwenidAndVisitorid(noteid, userBo.getId());
		boolean isThumsup = false;
		if (null == thumbsupBo) {
			thumbsupBo = new ThumbsupBo();
			thumbsupBo.setType(Constant.NOTE_TYPE);
			thumbsupBo.setOwner_id(noteid);
			thumbsupBo.setImage(userBo.getHeadPictureName());
			thumbsupBo.setVisitor_id(userBo.getId());
			thumbsupBo.setCreateuid(userBo.getId());
			thumbsupService.insert(thumbsupBo);
			isThumsup = true;
		} else {
			if (thumbsupBo.getDeleted() == Constant.DELETED) {
				thumbsupService.udateDeleteById(thumbsupBo.getId());
				isThumsup = true;
			}
		}
		updateCircleHot(circleService, redisServer, noteBo.getCircleId(), 1, Constant.CIRCLE_THUMP );
		if (isThumsup) {
			updateCount(noteid, Constant.THUMPSUB_NUM, 1);
		}
		updateCircieUnReadNum(noteBo.getCreateuid(), noteBo.getCircleId());
		updateDynamicNums(noteBo.getCreateuid(), 1, dynamicService, redisServer);
		String path = "/note/note-info.do?noteid=" + noteid;
		JPushUtil.pushMessage(pushTitle, "有人刚刚赞了你的帖子，快去看看吧!", path,  noteBo.getCreateuid());
		return Constant.COM_RESP;
	}

	@RequestMapping("/cancal-thumbsup")
	@ResponseBody
	public String cancelThumbsup(String noteid, HttpServletRequest request, HttpServletResponse response){
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(noteid, userBo.getId());
		if (thumbsupBo != null) {
			thumbsupService.deleteById(thumbsupBo.getId());
			NoteBo noteBo = noteService.selectById(noteid);
			updateCircleHot(circleService, redisServer, noteBo.getCircleId(), -1, Constant.CIRCLE_THUMP );
			updateCount(noteid, Constant.THUMPSUB_NUM, -1);
			updateDynamicNums(noteBo.getCreateuid(), -1, dynamicService, redisServer);
		}
		return Constant.COM_RESP;
	}

	@RequestMapping("/note-info")
	@ResponseBody
	public String noteInfo(String noteid, HttpServletRequest request, HttpServletResponse response) {
		NoteBo noteBo = noteService.selectById(noteid);
		if (null == noteBo) {
			return CommonUtil.toErrorResult(
					ERRORCODE.NOTE_IS_NULL.getIndex(),
					ERRORCODE.NOTE_IS_NULL.getReason());
		}
		UserBo userBo = getUserLogin(request);
		ThumbsupBo thumbsupBo = null;
		if (userBo != null) {
			updateHistory(userBo.getId(), noteBo.getCircleId(), locationService, circleService);
			thumbsupBo = thumbsupService.getByVidAndVisitorid(noteid, userBo.getId());
		}

		updateCircleHot(circleService, redisServer, noteBo.getCircleId(), 1, Constant.CIRCLE_NOTE_VISIT);
		updateCount(noteid, Constant.VISIT_NUM, 1);
		NoteVo noteVo = new NoteVo();
		String userid = userBo != null ? userBo.getId() : "";
		boToVo(noteBo, noteVo, userService.getUser(noteBo.getCreateuid()),userid);
		CircleBo circleBo = circleService.selectByIdIgnoreDel(noteBo.getCircleId());
		if (circleBo != null) {
			noteVo.setCirName(circleBo.getName());
			noteVo.setCirHeadPic(circleBo.getHeadPicture());
		}
		//这个帖子自己是否点赞
		noteVo.setMyThumbsup(null != thumbsupBo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("noteVo", noteVo);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 最新动态帖子
	 */
	@RequestMapping("/new-situation")
	@ResponseBody
	public String newSituation(String circleid, String start_id, boolean gt, int limit,
							   HttpServletRequest request, HttpServletResponse response) {
		List<NoteBo> noteBos = noteService.finyByCreateTime(circleid,start_id,gt,limit);
		List<NoteVo> noteVos = new LinkedList<>();
		UserBo loginUser = getUserLogin(request);
		String userid = loginUser != null ? loginUser.getId() : "";
		if (noteBos != null) {
			vosToList(noteBos, noteVos, userid);
		}
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("noteVoList", noteVos);
        return JSONObject.fromObject(map).toString();
	}


	/**
	 * 精华帖子，（字数100以上,按浏览量倒序，取消）,取前10
	 */
    @RequestMapping("/essential-note")
    @ResponseBody
    public String bestNote(String circleid, String start_id, int limit, HttpServletRequest request,
                           HttpServletResponse response) {
		List<NoteBo> noteBos = noteService.findByTopEssence(circleid, Constant.NOTE_JIAJING, start_id, limit);
		List<NoteVo> noteVoList = new LinkedList<>();
		UserBo loginUser = getUserLogin(request);
		String userid = loginUser != null ? loginUser.getId() : "";
		vosToList(noteBos, noteVoList, userid);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("noteVoList", noteVoList);
        return JSONObject.fromObject(map).toString();
    }

	/**
	 * 获取置顶帖子，（置顶帖子条件，字数>=200, 图片>=3, 取消）时间倒序取前2
	 * @return
	 */
	@RequestMapping("/top-notes")
	@ResponseBody
	public String topNotes(String circleid, String start_id, int limit, HttpServletRequest request,
						   HttpServletResponse response) {
		if (limit < 1) {
			limit = 2;
		}
		UserBo loginUser = getUserLogin(request);
		String userid = loginUser != null ? loginUser.getId() : "";
		List<NoteBo> noteBos = noteService.findByTopEssence(circleid, Constant.NOTE_TOP, start_id, limit);
		List<NoteVo> noteVoList = new LinkedList<>();
		if (noteBos != null) {
			vosToList(noteBos, noteVoList, userid);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("noteVoList", noteVoList);
		return JSONObject.fromObject(map).toString();
	}




	/**
	 * 热门详情；1周内帖子的阅读数+赞数+转发数+评论数最多的列表，取前10
	 * @return
	 */
    @RequestMapping("/hot-notes")
    @ResponseBody
    public String hotNotes(String circleid,HttpServletRequest request,
                           HttpServletResponse response) {
        List<NoteBo> noteBos = noteService.selectHotNotes(circleid);
		List<NoteVo> noteVoList = new LinkedList<>();
		UserBo loginUser = getUserLogin(request);
		String userid = loginUser != null ? loginUser.getId() : "";
		for (NoteBo noteBo : noteBos) {
			vosToList(noteBos, noteVoList, userid);
		}
        Map<String, Object> map = new HashMap<>();
        map.put("ret", 0);
        map.put("noteVoList", noteVoList);
        return JSONObject.fromObject(map).toString();
    }

	/**
	 * 评论帖子或者回复评论
	 * @return
	 */
	@RequestMapping("/add-comment")
	@ResponseBody
	public String addComment(@RequestParam(required = true)String circleid,
							 @RequestParam(required = true) String noteid,
							 @RequestParam(required = true) String countent,
							 String parentid, HttpServletRequest request, HttpServletResponse response){
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		NoteBo noteBo = noteService.selectById(noteid);
		if (noteBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.NOTE_IS_NULL.getIndex(),
					ERRORCODE.NOTE_IS_NULL.getReason());
		}
		updateHistory(userBo.getId(), noteBo.getCircleId(), locationService, circleService);
		Date currentDate = new Date();
		CommentBo commentBo = new CommentBo();
		commentBo.setNoteid(noteBo.getId());
		commentBo.setParentid(parentid);
		commentBo.setUserName(userBo.getUserName());
		commentBo.setContent(countent);
		commentBo.setType(Constant.NOTE_TYPE);
		commentBo.setCreateuid(userBo.getId());
		commentBo.setOwnerid(noteBo.getCreateuid());
		commentBo.setCreateTime(currentDate);
		commentService.insert(commentBo);

		updateCount(noteid, Constant.COMMENT_NUM, 1);
		userService.addUserLevel(userBo.getId(),1, Constant.LEVEL_COMMENT);
		updateCircleHot(circleService, redisServer, noteBo.getCircleId(), 1, Constant.CIRCLE_COMMENT);
		updateRedStar(userBo, noteBo, circleid, currentDate);
		updateDynamicNums(noteBo.getCreateuid(), 1, dynamicService, redisServer);
		updateCircieUnReadNum(noteBo.getCreateuid(), circleid);
		String path = "/note/note-info.do?noteid=" + noteid;
		JPushUtil.pushMessage(pushTitle, "有人刚刚评论了你的帖子，快去看看吧!", path,  noteBo.getCreateuid());
		if (!StringUtils.isEmpty(parentid)) {
			CommentBo comment = commentService.findById(parentid);
			if (comment != null) {
				updateCircieUnReadNum(comment.getCreateuid(), circleid);
				JPushUtil.pushMessage(pushTitle, "有人刚刚回复了你的评论，快去看看吧!", path,  comment.getCreateuid());
			}
		}
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("commentVo", comentBo2Vo(commentBo));
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 更新红人信息
	 * @param userBo
	 * @param noteBo
	 * @param circleid
	 * @param currentDate
	 */
	@Async
	private void updateRedStar(UserBo userBo, NoteBo noteBo, String circleid, Date currentDate){
		RedstarBo redstarBo = commentService.findRedstarBo(userBo.getId(), circleid);
		int curretWeekNo = CommonUtil.getWeekOfYear(currentDate);
		int year = CommonUtil.getYear(currentDate);
		if (redstarBo == null) {
			redstarBo = setRedstarBo(userBo.getId(), circleid, curretWeekNo, year);
			commentService.insertRedstar(redstarBo);
		}
		//判断贴的作者是不是自己
		boolean isNotSelf = !userBo.getId().equals(noteBo.getCreateuid());
		boolean isNoteUserCurrWeek = true;
		//如果帖子作者不是自己
		if (isNotSelf) {
			//帖子作者没有红人数据信息，则添加
			RedstarBo noteRedstarBo = commentService.findRedstarBo(noteBo.getCreateuid(), circleid);
			if (noteRedstarBo == null) {
				noteRedstarBo = setRedstarBo(noteBo.getCreateuid(), circleid, curretWeekNo, year);
				commentService.insertRedstar(noteRedstarBo);
			} else {
				//判断帖子作者周榜是不是当前周，是则添加数据，不是则更新周榜数据
				isNoteUserCurrWeek = (year == noteRedstarBo.getYear() && curretWeekNo == noteRedstarBo.getWeekNo());
			}
		}
		//判断自己周榜是不是同一周，是则添加数据，不是则更新周榜数据
		boolean isCurrentWeek = (year == redstarBo.getYear() && curretWeekNo == redstarBo.getWeekNo());
		//更新自己或他人红人评论数量，需要加锁，保证数据准确
		RLock lock = redisServer.getRLock(Constant.COMOMENT_LOCK);
		try {
			lock.lock(5, TimeUnit.SECONDS);
			//更新帖子评论数
			noteService.updateCommentCount(noteBo.getId(),1);
			//更新自己的红人信息
			if (isCurrentWeek) {
				commentService.addRadstarCount(userBo.getId(), circleid);
			} else {
				commentService.updateRedWeekByUser(userBo.getId(), curretWeekNo, year);
			}
			if (isNotSelf) {
				//更新帖子作者的红人信息
				if (isNoteUserCurrWeek) {
					commentService.addRadstarCount(noteBo.getCreateuid(), circleid);
				} else {
					commentService.updateRedWeekByUser(noteBo.getCreateuid(), curretWeekNo, year);
				}
			}
		} finally {
			lock.unlock();
		}
	}


	/**
	 * 删除自己的帖子评论
	 * @return
	 */
	@RequestMapping("/delete-self-comment")
	@ResponseBody
	public String deleteComments(String commentid,HttpServletRequest request,  HttpServletResponse response) {

		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CommentBo commentBo = commentService.findById(commentid);
		if (commentBo != null) {
			if (userBo.getId().equals(commentBo.getCreateuid())) {
				commentService.delete(commentid);
				updateCount(commentBo.getNoteid(), Constant.VISIT_NUM, 1);
			} else {
				return CommonUtil.toErrorResult(ERRORCODE.NOTE_NOT_MASTER.getIndex(),
						ERRORCODE.NOTE_NOT_MASTER.getReason());
			}
		}
		return Constant.COM_RESP;
	}

	/**
	 * 获取帖子评论
	 * @return
	 */
	@RequestMapping("/get-comments")
	@ResponseBody
	public String getComments(String noteid, String start_id, boolean gt, int limit,
							  HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo = getUserLogin(request);
		NoteBo noteBo = noteService.selectById(noteid);
		if (noteBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.NOTE_IS_NULL.getIndex(),
					ERRORCODE.NOTE_IS_NULL.getReason());
		}
		String userid = "";
		boolean isLogin = userBo != null;
		if (isLogin){
			updateHistory(userBo.getId(), noteBo.getCircleId(), locationService, circleService);
			userid = userBo.getId();
		}

		List<CommentBo> commentBos = commentService.selectByNoteid(noteid, start_id, gt, limit);
		List<CommentVo> commentVos = new ArrayList<>();
		for (CommentBo commentBo : commentBos) {
			CommentVo commentVo = comentBo2Vo(commentBo);
			ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(commentBo.getId(), userid);
			commentVo.setMyThumbsup(thumbsupBo != null);
			commentVo.setThumpsubCount(commentBo.getThumpsubNum());
			if (!StringUtils.isEmpty(commentBo.getParentid())) {
				CommentBo parent = commentService.findById(commentBo.getParentid());
				if (isLogin && !userid.equals(commentBo.getParentid())) {
					FriendsBo bo = friendsService.getFriendByIdAndVisitorIdAgree(userid, commentBo.getParentid());
					if (bo == null || StringUtils.isEmpty(bo.getBackname())) {
						commentVo.setParentUserName(parent.getUserName());
					} else {
						commentVo.setParentUserName(bo.getBackname());
					}
				} else {
					commentVo.setParentUserName(parent.getUserName());
				}
				commentVo.setParentUserid(parent.getCreateuid());
			}
			UserBo comUser = userService.getUser(commentBo.getCreateuid());
			if (isLogin && !userid.equals(commentBo.getCreateuid())) {
				FriendsBo bo = friendsService.getFriendByIdAndVisitorIdAgree(userid,commentBo.getCreateuid());
				if (bo == null || StringUtils.isEmpty(bo.getBackname())) {
					commentVo.setUserName(comUser.getUserName());
				} else {
					commentVo.setUserName(bo.getBackname());
				}
			} else {
				commentVo.setUserName(commentBo.getUserName());
			}
			commentVo.setUserHeadPic(comUser.getHeadPictureName());
			commentVo.setUserid(commentBo.getCreateuid());
			commentVo.setUserBirth(comUser.getBirthDay());
			commentVo.setUserSex(comUser.getSex());
			commentVo.setUserLevel(comUser.getLevel());
			commentVos.add(commentVo);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("commentVoList", commentVos);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 获取自己的所有评论
	 * @return
	 */
	@RequestMapping("/get-self-comments")
	@ResponseBody
	public String getSelfComments(String start_id, boolean gt, int limit,
								  HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		List<CommentBo> commentBos = commentService.selectByUser(userBo.getId(), start_id, gt, limit);
		List<CommentVo> commentVos = new ArrayList<>();
		for (CommentBo commentBo : commentBos) {
			commentVos.add(comentBo2Vo(commentBo));
		}
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("commentVoList", commentVos);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 获取自己评论过别人的帖子
	 * @return
	 */
	@RequestMapping("/my-comment-notes")
	@ResponseBody
	public String getMyCommentNotes(String start_id, boolean gt, int limit,
								  HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		String userid = userBo.getId();
		List<BasicDBObject> objects = commentService.selectMyNoteReply(userBo.getId(),start_id,limit );
		List<NoteVo> noteVoList = new LinkedList<>();
		for (BasicDBObject object : objects) {
			String id = object.get("noteid").toString();
			NoteBo noteBo = noteService.selectById(id);

			CircleBo circleBo = circleService.selectById(noteBo.getCircleId());
			NoteVo noteVo = new NoteVo();
			noteVo.setCirName(circleBo.getName());
			noteVo.setCirHeadPic(circleBo.getHeadPicture());
			noteVo.setCirNoteNum(circleBo.getNoteSize());
			noteVo.setCirVisitNum(circleBo.getVisitNum());
			UserBo author = userService.getUser(noteBo.getCreateuid());
			boToVo(noteBo, noteVo, author, userid);
			noteVo.setMyThumbsup(hasThumbsup(userid, noteBo.getId()));
			noteVoList.add(noteVo);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("noteVoList", noteVoList);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 评论点赞或取消点赞
	 * @return
	 */
	@RequestMapping("/comment-thumbsup")
	@ResponseBody
	public String commentThumbsup(String commentid, boolean isThumnbsup, HttpServletRequest request,
								  HttpServletResponse
			response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		ThumbsupBo thumbsupBo = thumbsupService.findHaveOwenidAndVisitorid(commentid, userBo.getId());
		int num = 0;
		if (isThumnbsup) {
			if (null == thumbsupBo) {
				thumbsupBo = new ThumbsupBo();
				thumbsupBo.setType(Constant.NOTE_COM_TYPE);
				thumbsupBo.setOwner_id(commentid);
				thumbsupBo.setImage(userBo.getHeadPictureName());
				thumbsupBo.setVisitor_id(userBo.getId());
				thumbsupBo.setCreateuid(userBo.getId());
				thumbsupService.insert(thumbsupBo);
				num ++;
			} else {
				if (thumbsupBo.getDeleted() == Constant.DELETED) {
					thumbsupService.udateDeleteById(thumbsupBo.getId());
					num++;
				}
			}
		} else {
			if (null != thumbsupBo && thumbsupBo.getDeleted() == Constant.ACTIVITY) {
				thumbsupService.deleteById(thumbsupBo.getId());
				num--;
			}
		}
		updateCommentThumbsup(commentid, num);
		return Constant.COM_RESP;
	}

	/**
	 * 点赞
	 * @param commentid
	 * @param num
	 */
	@Async
	private void updateCommentThumbsup(String commentid, int num){
		if (num != 0){
			RLock lock = redisServer.getRLock(commentid);
			try {
				lock.lock(1, TimeUnit.SECONDS);
				commentService.updateThumpsubNum(commentid, num);
			} finally {
				lock.unlock();
			}
		}
		if (num > 0) {
			CommentBo commentBo = commentService.findById(commentid);
			if (commentBo != null &&  commentBo.getType() == Constant.NOTE_TYPE) {
				NoteBo noteBo = noteService.selectById(commentBo.getNoteid());
				if (noteBo != null) {
					updateCircieUnReadNum(commentBo.getCreateuid(), noteBo.getCircleId());
				}
			}
		}
	}


	/**
	 * 获取帖子点赞列表
	 * @return
	 */
	@RequestMapping("/get-note-thumbsups")
	@ResponseBody
	public String getNoteThumbsups(String noteid, String start_id, boolean gt, int limit,
									HttpServletRequest request, HttpServletResponse response) {
		List<ThumbsupBo> thumbsupBos = thumbsupService.selectByOwnerIdPaged(
				start_id, gt, limit, noteid, Constant.NOTE_TYPE);
		List<UserBaseVo> userBaseVos = new ArrayList<>();
		for (ThumbsupBo thumbsupBo : thumbsupBos) {
			UserBo user = userService.getUser(thumbsupBo.getVisitor_id());
			if (user != null) {
				UserThumbsupVo userBaseVo = new UserThumbsupVo();
				BeanUtils.copyProperties(user, userBaseVo);
				userBaseVo.setThumbsupTime(thumbsupBo.getCreateTime());
				userBaseVos.add(userBaseVo);
			}
		}
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("userVoList", userBaseVos);
		return JSONObject.fromObject(map).toString();
	}


	/**
	 * 我的帖子
	 * @return
	 */
	@RequestMapping("/my-notes")
	@ResponseBody
	public String myNotes(String start_id, boolean gt, int limit, HttpServletRequest request,
						   HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		List<NoteBo> noteBos = noteService.selectMyNotes(userBo.getId(), start_id, gt, limit);
		if (noteBos != null && !noteBos.isEmpty()){
			updateHistory(userBo.getId(), noteBos.get(0).getCircleId(), locationService, circleService);
		}
		List<NoteVo> noteVoList = new LinkedList<>();
		NoteVo noteVo = null;
		for (NoteBo noteBo : noteBos) {
			noteVo = new NoteVo();
			CircleBo circleBo = circleService.selectById(noteBo.getCircleId());
			noteVo.setCirName(circleBo.getName());
			noteVo.setCirNoteNum(circleBo.getNoteSize());
			noteVo.setCirHeadPic(circleBo.getHeadPicture());
			noteVo.setCirVisitNum(circleBo.getVisitNum());
			boToVo(noteBo, noteVo, userBo, "");
			noteVo.setMyThumbsup(hasThumbsup(userBo.getId(), noteBo.getId()));
			noteVoList.add(noteVo);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("noteVoList", noteVoList);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 圈主删除帖子
	 * @return
	 */
	@RequestMapping("/delete-circle-notes")
	@ResponseBody
	public String deleteNotes(@RequestParam String noteids, @RequestParam String circleid, HttpServletRequest request,
						  HttpServletResponse response) {
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
		updateHistory(userBo.getId(), circleBo.getId(), locationService, circleService);
		String[] ids = CommonUtil.getIds(noteids);
		int notes = 0;
		if (circleBo.getCreateuid().equals(userBo.getId()) ||
				circleBo.getMasters().contains(userBo.getId())) {
			for (String id : ids) {
				NoteBo noteBo = noteService.selectById(id);
				if (null != noteBo) {
					//圈主删除帖子
					noteService.deleteNote(id, userBo.getId());
					commentService.deleteByNote(id);
					notes ++;
				}
			}
			if (notes != 0) {
				RLock lock = redisServer.getRLock(circleBo.getId()+"noteSize");
				try {
					lock.lock(2,TimeUnit.SECONDS);
					circleService.updateNotes(circleid, -notes);
				} finally {
					lock.unlock();
				}
			}
		}  else {
			return CommonUtil.toErrorResult(
					ERRORCODE.NOTE_NOT_MASTER.getIndex(),
					ERRORCODE.NOTE_NOT_MASTER.getReason());
		}
		return Constant.COM_RESP;
	}


	/**
	 * 个人删除自己的帖子
	 * @return
	 */
	@RequestMapping("/delete-my-notes")
	@ResponseBody
	public String deleteMyNotes(@RequestParam String noteids,HttpServletRequest request,
							  HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		String[] ids = CommonUtil.getIds(noteids);
		int notes = 0;
		CircleBo circleBo = null;
		for (String id : ids) {
			NoteBo noteBo = noteService.selectById(id);
			if (null != noteBo) {
				if (circleBo == null) {
					circleBo = circleService.selectById(noteBo.getCircleId());
				}
				//删除帖子
				if (noteBo.getCreateuid().equals(userBo.getId())) {
					noteService.deleteNote(id,userBo.getId());
					commentService.deleteByNote(id);
					notes ++;
				}
			}
		}
		if (circleBo != null && notes != 0) {
			RLock lock = redisServer.getRLock(circleBo.getId()+"noteSize");
			try {
				lock.lock(2,TimeUnit.SECONDS);
				circleService.updateNotes(circleBo.getId(), -notes);
			} finally {
				lock.unlock();
			}
		}
		return Constant.COM_RESP;
	}

	/**
	 * 圈子内帖子
	 */
	@RequestMapping("/circle-notes")
	@ResponseBody
	public String ciecleNotes(@RequestParam String circleid,
							  String start_id, int limit,
							  HttpServletRequest request, HttpServletResponse response) {
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_IS_NULL.getIndex(),
					ERRORCODE.CIRCLE_IS_NULL.getReason());
		}
		UserBo loginUser = getUserLogin(request);
		String userid = loginUser == null ? "" : loginUser.getId();
		List<NoteBo> noteBos = noteService.selectCircleNotes(circleid, start_id, limit);
		List<NoteVo> noteVoList = new LinkedList<>();
		if (noteBos != null) {
			vosToList(noteBos, noteVoList, userid);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("noteVoList", noteVoList);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 圈子管理员加精帖子
	 */
	@RequestMapping("/set-essence")
	@ResponseBody
	public String setEssence(@RequestParam String circleid, @RequestParam String noteid, int essence,
							  HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo.getCreateuid().equals(userBo.getId()) ||
				circleBo.getMasters().contains(userBo.getId())) {
			noteService.updateToporEssence(noteid, essence, Constant.NOTE_JIAJING);
		} else {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_MASTER_NULL.getIndex(),
					ERRORCODE.CIRCLE_MASTER_NULL.getReason());
		}
		return Constant.COM_RESP;
	}


	/**
	 * 圈子管理员置顶帖子
	 */
	@RequestMapping("/set-top")
	@ResponseBody
	public String setTopNotes(@RequestParam String circleid, @RequestParam String noteid, int top,
							  HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo.getCreateuid().equals(userBo.getId()) ||
				circleBo.getMasters().contains(userBo.getId())) {
			noteService.updateToporEssence(noteid, top, Constant.NOTE_TOP);
		} else {
			return CommonUtil.toErrorResult(
					ERRORCODE.CIRCLE_MASTER_NULL.getIndex(),
					ERRORCODE.CIRCLE_MASTER_NULL.getReason());
		}
		return Constant.COM_RESP;
	}


	/**
	 * 置顶和精华帖子
	 */
	@RequestMapping("/top-essence")
	@ResponseBody
	public String topAndessence(String circleid, String start_id, int limit, HttpServletRequest request,
						   HttpServletResponse response) {
		UserBo loginUser = getUserLogin(request);
		String userid = loginUser == null ? "" : loginUser.getId();
		List<NoteBo> noteBos = noteService.findByTopAndEssence(circleid, start_id, limit);
		List<NoteVo> noteVoList = new LinkedList<>();
		if (noteBos != null) {
			vosToList(noteBos, noteVoList, userid);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("noteVoList", noteVoList);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 转发到我的动态
	 */
	@RequestMapping("/forward-dynamic")
	@ResponseBody
	public String forwardDynamic(String noteid, String view, HttpServletRequest request,
								HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		NoteBo noteBo = noteService.selectById(noteid);
		if (null == noteBo) {
			return CommonUtil.toErrorResult(ERRORCODE.NOTE_IS_NULL.getIndex(),
					ERRORCODE.NOTE_IS_NULL.getReason());
		}
		DynamicBo dynamicBo = new DynamicBo();
		dynamicBo.setTitle(noteBo.getSubject());
		dynamicBo.setView(view);
		dynamicBo.setMsgid(noteid);
		dynamicBo.setCreateuid(userBo.getId());
		dynamicBo.setOwner(noteBo.getCreateuid());
		dynamicBo.setPhotos(new LinkedHashSet<>(noteBo.getPhotos()));
		dynamicBo.setType(Constant.NOTE_TYPE);
		CircleBo circleBo = circleService.selectById(noteBo.getCircleId());
		if (circleBo != null) {
			dynamicBo.setSourceName(circleBo.getName());
		}
		dynamicBo.setCreateuid(userBo.getId());
		dynamicService.addDynamic(dynamicBo);
		updateCount(noteid, Constant.SHARE_NUM, 1);
		updateDynamicNums(userBo.getId(), 1,dynamicService, redisServer);
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("dynamicid", dynamicBo.getId());
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 收藏帖子
	 * @param noteid
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/col-note")
	@ResponseBody
	public String colNotes(String noteid, HttpServletRequest request, HttpServletResponse response){
		UserBo userBo = getUserLogin(request);
		if (userBo == null) {
			return CommonUtil.toErrorResult(ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
        CollectBo chatBo = collectService.findByUseridAndTargetid(userBo.getId(), noteid);
        if (chatBo != null) {
            return CommonUtil.toErrorResult(ERRORCODE.COLLECT_EXIST.getIndex(),
                    ERRORCODE.COLLECT_EXIST.getReason());
        }
		NoteBo noteBo = noteService.selectById(noteid);
		chatBo = new CollectBo();
		chatBo.setCreateuid(userBo.getId());
		chatBo.setUserid(userBo.getId());
		chatBo.setTargetid(noteid);
		chatBo.setType(Constant.COLLET_URL);
		chatBo.setSub_type(Constant.NOTE_TYPE);
		chatBo.setTitle(noteBo.getSubject());
		CircleBo circleBo = circleService.selectById(noteBo.getCircleId());
		if (circleBo != null) {
			chatBo.setSource(circleBo.getName());
			chatBo.setSourceType(5);
		}
		collectService.insert(chatBo);
		updateCount(noteid, Constant.COLLECT_NUM, 1);
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("col-time", CommonUtil.time2str(chatBo.getCreateTime()));
		return JSONObject.fromObject(map).toString();
	}


	private RedstarBo setRedstarBo(String userid, String circleid, int weekNo, int year){
		RedstarBo redstarBo = new RedstarBo();
		redstarBo.setUserid(userid);
		redstarBo.setCommentTotal((long) 1);
		redstarBo.setCommentWeek((long) 1);
		redstarBo.setWeekNo(weekNo);
		redstarBo.setCircleid(circleid);
		redstarBo.setYear(year);
		return redstarBo;
	}


	/**
	 * 
	 * @param noteBos
	 * @param noteVoList
	 * @param loginUserid
	 */
	private void vosToList(List<NoteBo> noteBos, List<NoteVo> noteVoList, String loginUserid){
		for (NoteBo noteBo : noteBos) {
			NoteVo noteVo = new NoteVo();
			UserBo userBo = userService.getUser(noteBo.getCreateuid());
			boToVo(noteBo, noteVo, userBo, loginUserid);
			noteVo.setMyThumbsup(hasThumbsup(loginUserid, noteBo.getId()));
			noteVoList.add(noteVo);
		}
	}

	/**
	 * 
	 * @param noteBo
	 * @param noteVo
	 * @param creatBo
	 * @param userid
	 */
	private void boToVo(NoteBo noteBo, NoteVo noteVo, UserBo creatBo, String userid){
		BeanUtils.copyProperties(noteBo, noteVo);
		if (creatBo!= null) {
			if (!"".equals(userid) && !userid.equals(creatBo.getId())) {
				FriendsBo friendsBo = friendsService.getFriendByIdAndVisitorIdAgree(userid, creatBo.getId());
				if (friendsBo != null && !StringUtils.isEmpty(friendsBo.getBackname())) {
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

	private CommentVo comentBo2Vo(CommentBo commentBo){
		CommentVo commentVo = new CommentVo();
		BeanUtils.copyProperties(commentBo, commentVo);
		commentVo.setCommentId(commentBo.getId());
		commentVo.setUserid(commentBo.getCreateuid());
		return commentVo;
	}

	/**
	 * 判断前用户是否点赞
	 * @param loginUserid
	 * @param noteid
	 * @return
	 */
	private boolean hasThumbsup(String loginUserid, String noteid){
		if (!"".equals(loginUserid)) {
			ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(noteid, loginUserid);
			return thumbsupBo != null;
		}
		return false;
	}


	/**
	 *
	 * @param userid
	 * @param cirlceid
	 */
	@Async
	private void updateCircieUnReadNum(String userid, String cirlceid){

		RLock lock = redisServer.getRLock(userid + "UnReadNumLock");
		try{
			lock.lock(2, TimeUnit.SECONDS);
			ReasonBo reasonBo = reasonService.findByUserAndCircle(userid, cirlceid, Constant.ADD_AGREE);
			if (reasonBo == null) {
				reasonBo = new ReasonBo();
				reasonBo.setCircleid(cirlceid);
				reasonBo.setCreateuid(userid);
				reasonBo.setStatus(Constant.ADD_AGREE);
				reasonBo.setUnReadNum(1);
				reasonService.insert(reasonBo);
			} else {
				reasonService.updateUnReadNum(userid, cirlceid, 1);
			}
		} finally {
			lock.unlock();
		}
	}


	/**
	 * 圈子未读信息数量添加
	 * @param pushUserid
	 * @param circleid
	 */
	@Async
	private void updateCircieNoteUnReadNum(String pushUserid, String circleid){
		CircleBo circleBo = circleService.selectById(circleid);
		if (circleBo == null) {
			return;
		}
		HashSet<String> users = circleBo.getUsers();
		if (users.contains(pushUserid)) {
			users.remove(pushUserid);
		}
		logger.info(" circle {} note unRead update, users {}", circleid, users);
		RLock lock = redisServer.getRLock(circleid + "UnReadNumLock");
		try{
			lock.lock(3, TimeUnit.SECONDS);
			reasonService.updateUnReadNum(users, circleBo.getId());
		} finally {
			lock.unlock();
		}
	}

	@Async
	private void updateCount(String noteid, int type, int num){
		RLock lock = redisServer.getRLock(noteid.concat(String.valueOf(type)));
		try {
			lock.lock(2,TimeUnit.SECONDS);
			switch (type) {
				case Constant.VISIT_NUM:
					noteService.updateVisitCount(noteid);
					break;
				case Constant.COMMENT_NUM:
					noteService.updateCommentCount(noteid, num);
					break;
				case Constant.THUMPSUB_NUM:
					noteService.updateThumpsubCount(noteid, num);
					break;
				case Constant.SHARE_NUM:
					noteService.updateTransCount(noteid, num);
					break;
				case Constant.COLLECT_NUM:
					noteService.updateCollectCount(noteid, num);
					break;
				default:
					break;
			}
		} finally {
			lock.unlock();
		}
	}

}
