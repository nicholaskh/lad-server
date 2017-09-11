package com.lad.controller;

import com.lad.util.CommonUtil;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.RootLogger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@Controller
@RequestMapping("test")
public class TestController extends BaseContorller {

	private static Logger logger = RootLogger.getLogger(TestController.class);

	@RequestMapping("/send")
	@ResponseBody
	public void setTag(HttpServletRequest request, HttpServletResponse response) {
		String code = CommonUtil.getRandom();
		int res = CommonUtil.sendSMS2("15320542105", CommonUtil.buildCodeMsg(code));
		logger.info("SMS  message : ====== "  + res);
	}

	@RequestMapping("/ff-pic")
	@ResponseBody
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

}
