package com.lad.controller;

import com.lad.bo.*;
import com.lad.service.*;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.ERRORCODE;
import com.lad.vo.NoteVo;
import net.sf.json.JSONObject;
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

	@RequestMapping("/insert")
	@ResponseBody
	public String isnert(@RequestParam(required = true) double px,
			@RequestParam(required = true) double py,
			@RequestParam(required = true) String subject,
			@RequestParam(required = true) String landmark,
			@RequestParam(required = true) String content,
			@RequestParam(required = true) String circleid,
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
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		userBo = userService.getUser(userBo.getId());
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
        noteService.updateVisit(noteid, noteBo.getVisitcount()+1);


		return "";
	}


	@RequestMapping("/new-situation")
	@ResponseBody
	public String newSituation(String circleid, String startId, boolean gt, int limit,
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
		UserBo userBo = (UserBo) session.getAttribute("userBo");
		if (userBo == null) {
			return CommonUtil.toErrorResult(
					ERRORCODE.ACCOUNT_OFF_LINE.getIndex(),
					ERRORCODE.ACCOUNT_OFF_LINE.getReason());
		}
		List<NoteBo> noteBos = noteService.finyByCreateTime(circleid,startId,gt,limit);
        List<NoteVo> noteVoList = bo2vo(noteBos);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("noteVoList", noteVoList);
        return JSONObject.fromObject(map).toString();
	}


    @RequestMapping("/essential-note")
    @ResponseBody
    public String bestNote(String circleid,HttpServletRequest request,
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
        List<NoteBo> noteBos = noteService.selectByVisit(circleid);
        List<NoteVo> noteVoList = bo2vo(noteBos);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("noteVoList", noteVoList);
        return JSONObject.fromObject(map).toString();
    }

    @RequestMapping("/hot-note")
    @ResponseBody
    public String hotNote(String circleid,HttpServletRequest request,
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
        List<NoteBo> noteBos = noteService.selectByComment(circleid);
        List<NoteVo> noteVoList = bo2vo(noteBos);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", 0);
        map.put("noteVoList", noteVoList);
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
