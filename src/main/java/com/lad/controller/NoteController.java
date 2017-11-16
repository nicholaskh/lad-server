package com.lad.controller;

import com.lad.bo.*;
import com.lad.redis.RedisServer;
import com.lad.service.*;
import com.lad.util.*;
import com.lad.vo.CommentVo;
import com.lad.vo.NoteVo;
import com.lad.vo.UserBaseVo;
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

	private String pushTitle = "互动通知";


	@RequestMapping("/insert")
	@ResponseBody
	public String isnert(@RequestParam double px,
						 @RequestParam double py,
						 @RequestParam String subject,
						 @RequestParam(required = false)String landmark,
						 @RequestParam String content,
						 @RequestParam String circleid,
						 @RequestParam(required = false) MultipartFile[] pictures,
						 @RequestParam(required = false) String type,
			HttpServletRequest request, HttpServletResponse response) {
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
		NoteBo noteBo = new NoteBo();
		noteBo.setPosition(new double[] { px, py });
		noteBo.setLandmark(landmark);
		noteBo.setSubject(subject);
		noteBo.setContent(content);
		noteBo.setVisitcount(1);
		noteBo.setCreateuid(userBo.getId());
		noteBo.setCircleId(circleid);
		noteBo.setType(type);
		noteBo.setVisitcount(1);
		noteBo.setTemp(1);
		LinkedList<String> photos = new LinkedList<>();
		String userId =  userBo.getId();
		if (pictures != null) {
			Long time = Calendar.getInstance().getTimeInMillis();
			for (MultipartFile file : pictures) {
				String fileName = userId + "-" + time + "-"
						+ file.getOriginalFilename();
				System.out.println("----file: " + file.getOriginalFilename() + ",  size: " + file.getSize());
				logger.info(fileName);
				if ("video".equals(type)) {
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
		RLock lock = redisServer.getRLock("noteSize");
		try {
			lock.lock(2,TimeUnit.SECONDS);
			circleService.updateNotes(circleid, circleBo.getNoteSize() + 1);
		} finally {
			lock.unlock();
		}
		addDynamicMsgs(userId, noteBo.getId(), Constant.NOTE_TYPE, dynamicService);
		userService.addUserLevel(userBo.getId(), 1, Constant.LEVEL_NOTE);
		updateCircleHot(circleService, redisServer, circleid, 1, Constant.CIRCLE_NOTE);
		updateCircleHot(circleService, redisServer, circleid, 1, Constant.CIRCLE_NOTE_VISIT);
		updateDynamicNums(userId, 1, dynamicService, redisServer);
		NoteVo noteVo = new NoteVo();
		boToVo(noteBo, noteVo, userBo);
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
		Long time = Calendar.getInstance().getTimeInMillis();

		NoteBo noteBo = noteService.selectById(noteid);
		if (null == noteBo) {
			return CommonUtil.toErrorResult(ERRORCODE.NOTE_IS_NULL.getIndex(),
					ERRORCODE.NOTE_IS_NULL.getReason());
		}
		LinkedList<String> photos = noteBo.getPhotos();
		List<String> paths = new ArrayList<>();
		for (MultipartFile file : files) {
			String fileName = userId + "-" + time + "-"
					+ file.getOriginalFilename();
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
			RLock lock = redisServer.getRLock(Constant.THUMB_LOCK);
			try {
				lock.lock(2,TimeUnit.SECONDS);
				noteService.updateThumpsubCount(noteid, 1);
			} finally {
				lock.unlock();
			}
		}
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
			RLock lock = redisServer.getRLock(Constant.THUMB_LOCK);
			try {
				lock.lock(2,TimeUnit.SECONDS);
				noteService.updateThumpsubCount(noteid, -1);
			} finally {
				lock.unlock();
			}
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
		UserBo userBo;
		ThumbsupBo thumbsupBo = null;
		try {
			userBo = checkSession(request, userService);
			updateHistory(userBo.getId(), noteBo.getCircleId(), locationService, circleService);
			thumbsupBo = thumbsupService.getByVidAndVisitorid(userBo.getId(), noteid);
		} catch (MyException e) {
			logger.error(e);
			userBo = null;
		}
		updateCircleHot(circleService, redisServer, noteBo.getCircleId(), 1, Constant.CIRCLE_NOTE_VISIT);
		RLock lock = redisServer.getRLock(Constant.VISIT_LOCK);
		try {
			lock.lock(3,TimeUnit.SECONDS);
			noteService.updateVisitCount(noteid);
		} finally {
			lock.unlock();
		}
		NoteVo noteVo = new NoteVo();
		boToVo(noteBo, noteVo, userBo);
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
		if (noteBos != null) {
			for (NoteBo noteBo : noteBos) {
				NoteVo note = new NoteVo();
				UserBo userBo = userService.getUser(noteBo.getCreateuid());
				boToVo(noteBo, note, userBo);
				noteVos.add(note);
			}
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
		for (NoteBo noteBo : noteBos) {
			NoteVo noteVo = new NoteVo();
			UserBo userBo = userService.getUser(noteBo.getCreateuid());
			boToVo(noteBo, noteVo, userBo);
			noteVoList.add(noteVo);
		}
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
		List<NoteBo> noteBos = noteService.findByTopEssence(circleid, Constant.NOTE_TOP, start_id, limit);
		List<NoteVo> noteVoList = new LinkedList<>();
		if (noteBos != null) {
			for (NoteBo noteBo : noteBos) {
				NoteVo noteVo = new NoteVo();
				boToVo(noteBo, noteVo, userService.getUser(noteBo.getCreateuid()));
				noteVoList.add(noteVo);
			}
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
		for (NoteBo noteBo : noteBos) {
			NoteVo noteVo = new NoteVo();
			UserBo userBo = userService.getUser(noteBo.getCreateuid());
			boToVo(noteBo, noteVo, userBo);
			noteVoList.add(noteVo);
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
		commentBo.setCreateuid(userBo.getId());
		commentBo.setOwnerid(noteBo.getCreateuid());
		commentBo.setCreateTime(currentDate);
		commentService.insert(commentBo);

		userService.addUserLevel(userBo.getId(),1, Constant.LEVEL_COMMENT);
		updateCircleHot(circleService, redisServer, noteBo.getCircleId(), 1, Constant.CIRCLE_COMMENT);
		updateRedStar(userBo, noteBo, circleid, currentDate);
		updateDynamicNums(noteBo.getCreateuid(), 1, dynamicService, redisServer);

		String path = "/note/note-info.do?noteid=" + noteid;
		JPushUtil.pushMessage(pushTitle, "有人刚刚评论了你的帖子，快去看看吧!", path,  noteBo.getCreateuid());
		if (!StringUtils.isEmpty(parentid)) {
			CommentBo comment = commentService.findById(parentid);
			if (comment != null) {
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
				RLock lock = redisServer.getRLock(Constant.COMOMENT_LOCK);
				try {
					lock.lock(2, TimeUnit.SECONDS);
					//更新帖子评论数
					noteService.updateCommentCount(commentBo.getNoteid(), -1);
				} finally {
					lock.unlock();
				}
			} else {
				return CommonUtil.toErrorResult(
						ERRORCODE.NOTE_NOT_MASTER.getIndex(),
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
		List<CommentBo> commentBos = commentService.selectByNoteid(noteid, start_id, gt, limit);
		List<CommentVo> commentVos = new ArrayList<>();
		for (CommentBo commentBo : commentBos) {
			CommentVo commentVo = comentBo2Vo(commentBo);
			ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(commentBo.getId(), userBo.getId());
			commentVo.setMyThumbsup(thumbsupBo != null);
			long thums = thumbsupService.selectByOwnerIdCount(noteid);
			commentVo.setThumpsubCount(thums);
			if (!StringUtils.isEmpty(commentBo.getParentid())) {
				CommentBo parent = commentService.findById(commentBo.getParentid());
				commentVo.setParentUserName(parent.getUserName());
				commentVo.setParentUserid(parent.getCreateuid());
			}
			UserBo comUser = userService.getUser(commentBo.getCreateuid());
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
			boToVo(noteBo, noteVo, author);
			noteVoList.add(noteVo);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("noteVoList", noteVoList);
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 评论点赞
	 * @return
	 */
	@RequestMapping("/comment-thumbsup")
	@ResponseBody
	public String commentThumbsup(String commentid,HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		ThumbsupBo thumbsupBo = thumbsupService.findHaveOwenidAndVisitorid(commentid, userBo.getId());
		if (null == thumbsupBo) {
			thumbsupBo = new ThumbsupBo();
			thumbsupBo.setType(Constant.NOTE_COM_TYPE);
			thumbsupBo.setOwner_id(commentid);
			thumbsupBo.setImage(userBo.getHeadPictureName());
			thumbsupBo.setVisitor_id(userBo.getId());
			thumbsupBo.setCreateuid(userBo.getId());
			thumbsupService.insert(thumbsupBo);
		} else {
			if (thumbsupBo.getDeleted() == Constant.DELETED) {
				thumbsupService.udateDeleteById(thumbsupBo.getId());
			}
		}
		return Constant.COM_RESP;
	}


	/**
	 * 获取帖子点赞列表
	 * @return
	 */
	@RequestMapping("/get-note-thumbsups")
	@ResponseBody
	public String getNoteThumbsups(String noteid, String start_id, boolean gt, int limit,
									HttpServletRequest request, HttpServletResponse response) {
		UserBo userBo;
		try {
			userBo = checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		List<ThumbsupBo> thumbsupBos = thumbsupService.selectByOwnerIdPaged(
				start_id, gt, limit, noteid, Constant.NOTE_TYPE);

		List<UserBaseVo> userBaseVos = new ArrayList<>();
		for (ThumbsupBo thumbsupBo : thumbsupBos) {
			UserBo user = userService.getUser(thumbsupBo.getVisitor_id());
			if (user != null) {
				UserBaseVo userBaseVo = new UserBaseVo();
				BeanUtils.copyProperties(user, userBaseVo);
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
			userBo = userService.getUser(noteBo.getCreateuid());
			boToVo(noteBo, noteVo, userBo);
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
				RLock lock = redisServer.getRLock("noteSize");
				try {
					lock.lock(2,TimeUnit.SECONDS);
					circleService.updateNotes(circleid, circleBo.getNoteSize() - notes);
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
			RLock lock = redisServer.getRLock("noteSize");
			try {
				lock.lock(2,TimeUnit.SECONDS);
				circleService.updateNotes(circleBo.getId(), circleBo.getNoteSize() - notes);
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
		List<NoteBo> noteBos = noteService.selectCircleNotes(circleid, start_id, limit);
		List<NoteVo> noteVoList = new LinkedList<>();
		for (NoteBo noteBo : noteBos) {
			NoteVo noteVo = new NoteVo();
			boToVo(noteBo, noteVo, userService.getUser(noteBo.getCreateuid()));
			noteVoList.add(noteVo);
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


	private void boToVo(NoteBo noteBo, NoteVo noteVo, UserBo userBo){
		BeanUtils.copyProperties(noteBo, noteVo);
		if (userBo!= null) {
			noteVo.setSex(userBo.getSex());
			noteVo.setBirthDay(userBo.getBirthDay());
			noteVo.setHeadPictureName(userBo.getHeadPictureName());
			noteVo.setUsername(userBo.getUserName());
			noteVo.setUserLevel(userBo.getLevel());
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


}
