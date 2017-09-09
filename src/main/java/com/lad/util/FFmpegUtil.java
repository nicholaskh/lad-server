package com.lad.util;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.RootLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/9
 */
public class FFmpegUtil {
    private static Logger logger = RootLogger.getLogger(FFmpegUtil.class);
    /**
     *
     * @param inFile   视频文件
     * @param path
     */
    public static String transfer(File inFile, String path){

        String fileName = inFile.getName();
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
                logger.info(">" + line);
                System.out.println(">" + line);
            }
            br.close();
            isr.close();
            stderr.close();
        } catch (Exception e){
            outName = "";
            e.printStackTrace();
        } finally {
            if (proc != null){
                proc.destroy();
            }
        }
        return outName;
    }



}
