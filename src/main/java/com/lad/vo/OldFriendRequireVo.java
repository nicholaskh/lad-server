package com.lad.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class OldFriendRequireVo extends  BaseVo{
	private String sex;
	private String age;
	private String address;
	private Map<String,Set<String>> hobbys = new HashMap<>();// 兴趣,list
	private List<String> images = new ArrayList<>();
}
