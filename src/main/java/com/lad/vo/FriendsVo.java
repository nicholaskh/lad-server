package com.lad.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;

@Getter
@Setter
public class FriendsVo extends BaseVo {
	private String id;
	private String userid;
	private String friendid;
	private String backname;
	private List<String> tag;
	private HashSet<String> phone = new HashSet<>();
	private String description;
	private Integer VIP;
	private Integer black;
	private String username;
	private String picture;
	private String sex;

	private String channelId;

	private int apply;

	//关联账号的角色，true 表示当前主用户角色是父母，false表示当前主用户角色是子女
	private boolean parent;
	//关联账号状态，0表示普通好友， 1 表示发送关联申请，2 表示被申请用户的状态， 3表示已建立关联， -1  表示拒绝或取消
	private int relateStatus;
}
