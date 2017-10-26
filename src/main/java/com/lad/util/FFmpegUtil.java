package com.lad.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/9
 */
public class FFmpegUtil {

    private static Logger logger = LogManager.getLogger(FFmpegUtil.class);
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
        return FFmpeg(cmd.toString(), outName);
    }


    /**
     *
     * @param urls   视频文件
     * @param path
     */
    public static String inforTransfer(String urls, String path, String inforid){
        String outName = inforid + "-ffmpeg.jpg";
        StringBuilder cmd = new StringBuilder("ffmpeg -i ");
        cmd.append(urls);
        //截取视频第一秒的视频图片
        cmd.append(" -y -f image2 -ss 1 -t 0.001 -s ");
        cmd.append("1000x562 ");
        cmd.append(path).append(outName);
        return FFmpeg(cmd.toString(), outName);
    }

    private static String FFmpeg(String cmd, String outName){
        Runtime rt = Runtime.getRuntime();
        Process proc = null;
        InputStream stderr = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            proc= rt.exec(new String[]{"sh","-c",cmd});
            //调用线程命令进行转码
            stderr = proc.getErrorStream();
            isr = new InputStreamReader(stderr);
            br = new BufferedReader(isr);
            proc.waitFor();
            String line = "";
            while ((line = br.readLine()) != null) {
                logger.info(">" + line);
            }
            br.close();
            isr.close();
            stderr.close();
        } catch (Exception e){
            logger.error(e);
            return "";
        } finally {
            try {
                if (br != null){
                    br.close();
                }
                if (isr != null){
                    isr.close();
                }
                if (stderr != null){
                    stderr.close();
                }
            } catch (IOException e) {
                logger.error(e);
                return "";
            }
            if (proc != null){
                proc.destroy();
            }
        }
        return outName;
    }
}
