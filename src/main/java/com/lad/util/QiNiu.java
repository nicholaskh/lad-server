package com.lad.util;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class QiNiu {

	private static Logger logger = LogManager.getLogger(QiNiu.class);
	
	public static String uploadToQiNiu(String path ,String filename){
		//构造一个带指定Zone对象的配置类
		Configuration cfg = new Configuration(Zone.zone1());
		//...其他参数参考类注释

		UploadManager uploadManager = new UploadManager(cfg);
		//...生成上传凭证，然后准备上传
		String accessKey = "wDgkTBIuUn5KnvyFzuMIr8GdC1KCRnN4KABH7dF-";
		String secretKey = "kQUvoiTx0Odyjo1OUudAJXTlGxF1Nhk1eK7YHV1n";
		String bucket = "ladapp";
		//如果是Windows情况下，格式是 D:\\qiniu\\test.png
		String localFilePath = path+filename;
		String pathStr[] = path.split("/");
		String key = pathStr[pathStr.length-1]+"-"+filename;
		Auth auth = Auth.create(accessKey, secretKey);
		String upToken = auth.uploadToken(bucket, key);
		try {
			long start = System.currentTimeMillis();
		    uploadManager.put(localFilePath, key, upToken);
		    long end =  System.currentTimeMillis();
			logger.info("update time millis : ======= {}",(end-start));
		} catch (QiniuException ex) {
			logger.error(ex);
		}
		return key;
	}
	
	public static String uploadToQiNiuDue(String path ,String filename, int days){
		//构造一个带指定Zone对象的配置类
		Configuration cfg = new Configuration(Zone.zone1());
		//...其他参数参考类注释

		UploadManager uploadManager = new UploadManager(cfg);
		//...生成上传凭证，然后准备上传
		String accessKey = "wDgkTBIuUn5KnvyFzuMIr8GdC1KCRnN4KABH7dF-";
		String secretKey = "kQUvoiTx0Odyjo1OUudAJXTlGxF1Nhk1eK7YHV1n";
		String bucket = "ladapp";
		String localFilePath = path+filename;
		String pathStr[] = path.split("/");
		String key = pathStr[pathStr.length-1]+"-"+filename;
		Auth auth = Auth.create(accessKey, secretKey);
		String upToken = auth.uploadToken(bucket, key);
		try {
			long start = System.currentTimeMillis();
			uploadManager.put(localFilePath, key, upToken);
			long end =  System.currentTimeMillis();
			logger.info("update time millis due : ======= {}",(end-start));
		} catch (QiniuException ex) {
		    Response r = ex.response;
		    System.err.println(r.toString());
		}
		BucketManager bucketManager = new BucketManager(auth, cfg);
		try {
		    bucketManager.deleteAfterDays(bucket, key, days);
		} catch (QiniuException ex) {
			logger.error(ex);
		}
		return key;
	}
	
	public static void main(String[] args){
		Long startTie = System.currentTimeMillis();
		uploadToQiNiu("E:\\个人\\picture\\图片\\", "20150510_125219.jpg");

		System.out.println(System.currentTimeMillis() - startTie);
	}

}
