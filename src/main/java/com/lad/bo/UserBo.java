package com.lad.bo;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user")
public class UserBo extends BaseBo {
	
	private String address;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	private String userName;

	private String phone;

	private String sex;

	private String password;

	private String headPictureName;

	private String birthDay;

	private String personalizedSignature;

	private HashSet<String> chatrooms = new HashSet<String>();

	private List<String> circleTops = new LinkedList<>();

	private int level = 1;
	//个人动态页面背景图
	private String dynamicPic;

	//登录类型 0 普通注册用户， 1 微信授权， 2 QQ授权， 3 微博授权
	private int loginType;
	//授权用户唯一标识
	private String openid;
	//微信个人用户授权userinfo
	private String unionid;
	//授权的token
	private String accessToken;
	//
	private String scope;
	//
	private String refeshToken;
	//获取授权时间
	private String tokenTime;
	//授权token的有效持续时间 单位秒
	private long expiresTime;
	//授权的用户特权信息，json数组，如微信沃卡用户为（chinaunicom）
	private String privilege;

	private String province;

	private String city;

	//vip等级，0表示非vip
	private int vipLevel;

	//实名认证 类型 0 未实名认证， 1 身份证实名认证， 2 银行卡实名认证
	private int idCardType;
	//实名认证卡号
	private String idCardNo;
	//实名认证的图片
	private String idCardPic;
	//实名名称
	private String realName;

	private Date lastLoginTime;

	/**
	 * 面对面群聊
	 */
	private HashSet<String> faceChatrooms = new HashSet<String>();

	private LinkedList<String> chatroomsTop = new LinkedList<String>();
	/**
	 * 个人在前端显示的聊天室窗口
	 */
	private HashSet<String> showChatrooms = new LinkedHashSet<>();

	private String locationid;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHeadPictureName() {
		return headPictureName;
	}

	public void setHeadPictureName(String headPictureName) {
		this.headPictureName = headPictureName;
	}

	public String getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(String birthDay) {
		this.birthDay = birthDay;
	}

	public String getPersonalizedSignature() {
		return personalizedSignature;
	}

	public void setPersonalizedSignature(String personalizedSignature) {
		this.personalizedSignature = personalizedSignature;
	}

	public HashSet<String> getChatrooms() {
		return chatrooms;
	}

	public void setChatrooms(HashSet<String> chatrooms) {
		this.chatrooms = chatrooms;
	}

	public String getLocationid() {
		return locationid;
	}

	public void setLocationid(String locationid) {
		this.locationid = locationid;
	}

	public LinkedList<String> getChatroomsTop() {
		return chatroomsTop;
	}

	public void setChatroomsTop(LinkedList<String> chatroomsTop) {
		this.chatroomsTop = chatroomsTop;
	}

	public HashSet<String> getFaceChatrooms() {
		return faceChatrooms;
	}

	public void setFaceChatrooms(HashSet<String> faceChatrooms) {
		this.faceChatrooms = faceChatrooms;
	}

	public List<String> getCircleTops() {
		return circleTops;
	}

	public void setCircleTops(List<String> circleTops) {
		this.circleTops = circleTops;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getDynamicPic() {
		return dynamicPic;
	}

	public void setDynamicPic(String dynamicPic) {
		this.dynamicPic = dynamicPic;
	}

	public HashSet<String> getShowChatrooms() {
		return showChatrooms;
	}

	public void setShowChatrooms(HashSet<String> showChatrooms) {
		this.showChatrooms = showChatrooms;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getTokenTime() {
		return tokenTime;
	}

	public void setTokenTime(String tokenTime) {
		this.tokenTime = tokenTime;
	}

	public long getExpiresTime() {
		return expiresTime;
	}

	public void setExpiresTime(long expiresTime) {
		this.expiresTime = expiresTime;
	}

	public String getPrivilege() {
		return privilege;
	}

	public void setPrivilege(String privilege) {
		this.privilege = privilege;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public int getLoginType() {
		return loginType;
	}

	public void setLoginType(int loginType) {
		this.loginType = loginType;
	}

	public int getVipLevel() {
		return vipLevel;
	}

	public void setVipLevel(int vipLevel) {
		this.vipLevel = vipLevel;
	}

	public int getIdCardType() {
		return idCardType;
	}

	public void setIdCardType(int idCardType) {
		this.idCardType = idCardType;
	}

	public String getIdCardNo() {
		return idCardNo;
	}

	public void setIdCardNo(String idCardNo) {
		this.idCardNo = idCardNo;
	}

	public String getIdCardPic() {
		return idCardPic;
	}

	public void setIdCardPic(String idCardPic) {
		this.idCardPic = idCardPic;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getRefeshToken() {
		return refeshToken;
	}

	public void setRefeshToken(String refeshToken) {
		this.refeshToken = refeshToken;
	}
}
