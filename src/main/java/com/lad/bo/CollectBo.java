package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedHashSet;

/**
 * 收藏的聊天信息
 */
@Document(collection = "collect")
public class CollectBo extends BaseBo {
	
	private String content;
	
	private String userid;
	
	private String title;

	private String path;

	private int type;
	//子分类，在url中区分文章、帖子、聚会、圈子
	private int sub_type;

	private String targetid;
	//来源
	private String source;
	//来源类型 0 圈子， 1 资讯
	private int sourceType;
	//来源id，
	private String sourceid;
	//来源id，
	private String targetPic;

	private String video;

	//用户自定义分类
	private LinkedHashSet<String> userTags = new LinkedHashSet<>();


	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTargetid() {
		return targetid;
	}

	public void setTargetid(String targetid) {
		this.targetid = targetid;
	}

	public int getSub_type() {
		return sub_type;
	}

	public void setSub_type(int sub_type) {
		this.sub_type = sub_type;
	}

	public LinkedHashSet<String> getUserTags() {
		return userTags;
	}

	public void setUserTags(LinkedHashSet<String> userTags) {
		this.userTags = userTags;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public int getSourceType() {
		return sourceType;
	}

	public void setSourceType(int sourceType) {
		this.sourceType = sourceType;
	}

	public String getSourceid() {
		return sourceid;
	}

	public void setSourceid(String sourceid) {
		this.sourceid = sourceid;
	}

	public String getTargetPic() {
		return targetPic;
	}

	public void setTargetPic(String targetPic) {
		this.targetPic = targetPic;
	}

	public String getVideo() {
		return video;
	}

	public void setVideo(String video) {
		this.video = video;
	}
}
