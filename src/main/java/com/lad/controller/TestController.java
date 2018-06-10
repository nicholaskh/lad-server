package com.lad.controller;

import com.alibaba.fastjson.JSON;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.lad.util.JPushUtil;
import lombok.extern.log4j.Log4j2;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Log4j2
@RestController
@RequestMapping("test")
public class TestController extends BaseContorller {


	@GetMapping("/send")
	public String setTag(HttpServletRequest request, HttpServletResponse response) {
		String code = CommonUtil.getRandom();
		int res = CommonUtil.sendSMS2("15320542105", CommonUtil.buildCodeMsg(code));
		Map<String, Object> map = new HashMap<>();
		map.put("message", res);
		return JSON.toJSONString(map);
	}

	@GetMapping("/get-report")
	public String getSMSReport(int type, HttpServletRequest request, HttpServletResponse response) {
		String res = "";
		if (type == 0){
			res = CommonUtil.getSMSReport();
		} else {
			res = CommonUtil.getSMSReport2();
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("res", res);
		return JSONObject.fromObject(map).toString();
	}

	@GetMapping("/push-info")
	public String pushInfor(String userid, HttpServletRequest request, HttpServletResponse response) {
		String time = CommonUtil.getCurrentDate(new Date());
		JPushUtil.push("聚会通知", "通知我的测试信息，推送到人:" + time,"123..s", userid);
		return Constant.COM_RESP;
	}

	@GetMapping("/ff-pic")
	public String pic(HttpServletRequest request, HttpServletResponse response) {

		String path = "/home/dongensi/";
		String fileName = "myvideo.mp4";

		String outName = fileName.substring(0, fileName.lastIndexOf('.'))+"ffmpeg.jpg";
		StringBuilder cmd = new StringBuilder("ffmpeg -i ");
		cmd.append(path).append(fileName);
		//截取视频第一秒的视频图片
		cmd.append(" -y -f image2 -ss 1 -t 0.001 -s ");
		cmd.append("176x144 ");
		cmd.append(path).append(outName);

		Runtime rt = Runtime.getRuntime();
		Process proc = null;
		try {
			proc= rt.exec(new String[]{"sh","-c",cmd.toString()});
			//调用线程命令进行转码
			InputStream stderr = proc.getErrorStream();
			InputStreamReader isr = new InputStreamReader(stderr);
			BufferedReader br = new BufferedReader(isr);
			System.out.println("--------------ffmpeg start---------");
			proc.waitFor();
			System.out.println("--------------ffmpeg end---------");
			String line = "";
			while ((line = br.readLine()) != null) {
				System.out.println(">" + line);
			}
			br.close();
			isr.close();
			stderr.close();
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			if (proc != null){
				proc.destroy();
			}
		}
		return outName;
	}



	@PostMapping("/upload")
	public String upload(MultipartFile imfile) {
		if (imfile != null) {
			long time = Calendar.getInstance().getTimeInMillis();
			String fileName = String.format("59c37cea31f0a51f8c9d2e79-%d-%s",time, imfile.getOriginalFilename());
			log.info("===== start upload  imfile name : {}, imfile size: {}" , fileName, imfile.getSize());
			String[] path = CommonUtil.uploadVedio(imfile, Constant.NOTE_PICTURE_PATH, fileName, 0);
			long timeHas = System.currentTimeMillis()- time;
			log.info("===== end upload  imfile path : {}, update time : {}" , path, timeHas);
			Map<String, Object> map = new HashMap<>();
			map.put("time", timeHas);
			map.put("video", path[0]);
			map.put("videoPic", path[1]);
			return JSON.toJSONString(map);
		}
		return "error";
	}


}
