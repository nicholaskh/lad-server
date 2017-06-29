package com.lad.controller;

import com.lad.bo.*;
import com.lad.redis.RedisServer;
import com.lad.service.*;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.ERRORCODE;
import com.lad.util.MyException;
import com.lad.vo.NoteVo;
import net.sf.json.JSONObject;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("note")
public class NoteController extends BaseContorller {

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


	@RequestMapping("/insert")
	@ResponseBody
	public String isnert(@RequestParam(required = true) double px,
			@RequestParam(required = true) double py,
			@RequestParam(required = true) String subject,
			@RequestParam(required = true) String landmark,
			@RequestParam(required = true) String content,
			@RequestParam(required = true) String circleid,
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
		NoteBo noteBo = new NoteBo();
		noteBo.setPosition(new double[] { px, py });
		noteBo.setLandmark(landmark);
		noteBo.setSubject(subject);
		noteBo.setContent(content);
		noteBo.setVisitcount(1);
		noteBo.setCreateuid(userBo.getId());
		noteService.insert(noteBo);
		HashSet<String> notes = circleBo.getNotes();
		notes.add(noteBo.getId());
		circleService.updateNotes(circleBo.getId(), notes);
		return Constant.COM_RESP;
	}

	@RequestMapping("/photo")
	@ResponseBody
	public String head_picture(@RequestParam("photo") MultipartFile file,
			@RequestParam(required = true) String noteid,
			HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return "{\"ret\":20002,\"error\":\":未登录\"}";
		}
		if (session.getAttribute("isLogin") == null) {
			return "{\"ret\":20002,\"error\":\":未登录\"}";
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return "{\"ret\":20002,\"error\":\":未登录\"}";
		}
		userBo = userService.getUser(userBo.getId());
		String userId = userBo.getId();
		Long time = Calendar.getInstance().getTimeInMillis();
		String fileName = userId + "-" + time + "-"
				+ file.getOriginalFilename();
		String path = CommonUtil.upload(file, Constant.NOTE_PICTURE_PATH,
				fileName, 0);

		NoteBo noteBo = noteService.selectById(noteid);
		if (null == noteBo) {
			return CommonUtil.toErrorResult(ERRORCODE.NOTE_IS_NULL.getIndex(),
					ERRORCODE.NOTE_IS_NULL.getReason());
		}
		noteService.updatePhoto(noteid, path);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 0);
		map.put("path", path);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("/thumbsup")
	@ResponseBody
	public String thumbsup(String noteid, HttpServletRequest request, HttpServletResponse response){
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return "{\"ret\":20002,\"error\":\":未登录\"}";
		}
		if (session.getAttribute("isLogin") == null) {
			return "{\"ret\":20002,\"error\":\":未登录\"}";
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return "{\"ret\":20002,\"error\":\":未登录\"}";
		}
		userBo = userService.getUser(userBo.getId());
		ThumbsupBo thumbsupBo = new ThumbsupBo();
		thumbsupBo.setOwner_id(noteid);
		thumbsupBo.setVisitor_id(userBo.getId());
		thumbsupService.insert(thumbsupBo);
		return Constant.COM_RESP;
	}

	@RequestMapping("/cancal-thumbsup")
	@ResponseBody
	public String cancelThumbsup(String noteid, HttpServletRequest request, HttpServletResponse response){
		HttpSession session = request.getSession();
		if (session.isNew()) {
			return "{\"ret\":20002,\"error\":\":未登录\"}";
		}
		if (session.getAttribute("isLogin") == null) {
			return "{\"ret\":20002,\"error\":\":未登录\"}";
		}
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return "{\"ret\":20002,\"error\":\":未登录\"}";
		}
		userBo = userService.getUser(userBo.getId());
		ThumbsupBo thumbsupBo = thumbsupService.getByVidAndVisitorid(noteid, userBo.getId());
		if (thumbsupBo != null) {
			thumbsupBo.setDeleted(1);
			thumbsupService.insert(thumbsupBo);
		}
		return Constant.COM_RESP;
	}

	@RequestMapping("/note-info")
	@ResponseBody
	public String noteInfo(String noteid, HttpServletRequest request, HttpServletResponse response) {
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
		NoteBo noteBo = noteService.selectById(noteid);
		if (null == noteBo) {
			return CommonUtil.toErrorResult(
					ERRORCODE.NOTE_IS_NULL.getIndex(),
					ERRORCODE.NOTE_IS_NULL.getReason());
		}
        noteService.updateVisitCount(noteid);


		return "";
	}

	/**
	 * 最新动态帖子
	 */
	@RequestMapping("/new-situation")
	@ResponseBody
	public String newSituation(String circleid, String startId, boolean gt, int limit,
							   HttpServletRequest request, HttpServletResponse response) {
		try {
			checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
		List<NoteBo> noteBos = noteService.finyByCreateTime(circleid,startId,gt,limit);
        List<NoteVo> noteVoList = bo2vo(noteBos);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("noteVoList", noteVoList);
        return JSONObject.fromObject(map).toString();
	}

	/**
	 * 精华帖子，字数100以上,按浏览量倒序排,取前10
	 */
    @RequestMapping("/essential-note")
    @ResponseBody
    public String bestNote(String circleid,HttpServletRequest request,
                           HttpServletResponse response) {
		try {
			checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
        List<NoteBo> noteBos = noteService.selectByVisit(circleid);
        List<NoteVo> noteVoList = bo2vo(noteBos);
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
		try {
			checkSession(request, userService);
		} catch (MyException e) {
			return e.getMessage();
		}
        List<NoteBo> noteBos = noteService.selectByComment(circleid);
        List<NoteVo> noteVoList = bo2vo(noteBos);
        Map<String, Object> map = new HashMap<String, Object>();
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

		Date currentDate = new Date();
		CommentBo commentBo = new CommentBo();
		commentBo.setNoteid(noteBo.getId());
		commentBo.setParentid(parentid);
		commentBo.setContent(countent);
		commentBo.setCreateuid(userBo.getId());
		commentBo.setCreateTime(currentDate);

		RedstarBo redstarBo = commentService.findRedstarBo(userBo.getId(), circleid);

		int curretWeekNo = CommonUtil.getWeekOfYear(currentDate);

		if (redstarBo == null) {
			redstarBo = new RedstarBo();
			redstarBo.setUserid(commentBo.getCreateuid());
			redstarBo.setCommentTotal((long) 1);
			redstarBo.setCommentWeek((long) 1);
			redstarBo.setWeekNo(curretWeekNo);
			commentService.insert(commentBo, redstarBo);
		} else {
			 if (curretWeekNo != redstarBo.getWeekNo()) {
				 commentService.updateRedWeek(curretWeekNo);
			 }
			commentService.insert(commentBo);
		}
		//帖子的作者也需要更新评论数
		UserBo noteUserBo = userService.getUser(noteBo.getCreateuid());
		//如果是自己的帖子则不再添加
		if (noteUserBo != null && !noteUserBo.getId().equals(userBo.getId())) {
			RLock lock = redisServer.getRLock(Constant.COMOMENT_LOCK);
			try {
				lock.lock(3, TimeUnit.SECONDS);
				//更新另外一个user的红人评论数，此时需要加锁，保证数据同步
				commentService.updateCommmentCount(noteUserBo.getId(), circleid);
			} finally {
				lock.unlock();
			}
		}
		Map<String, Object> map = new HashMap<>();
		map.put("ret", 0);
		map.put("commentVo", commentBo);
		return JSONObject.fromObject(map).toString();
	}

    private List<NoteVo> bo2vo(List<NoteBo> noteBos){
        List<NoteVo> noteVoList = new LinkedList<>();
        NoteVo noteVo;
        for (NoteBo noteBo : noteBos) {
            noteVo = new NoteVo();
            BeanUtils.copyProperties(noteVo, noteBo);
            List<CommentBo> commentBos = commentService.selectByNoteid(noteBo.getId());
            if (commentBos != null) {
                noteVo.setCommontCount((long)commentBos.size());
            }
            noteVo.setVisitCount(noteBo.getVisitcount());
            noteVo.setNodeid(noteBo.getId());
            noteVo.setTransCount(noteBo.getTranscount());
        }
		return noteVoList;
	}


}
