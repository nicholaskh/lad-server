package com.lad.vo;

import java.util.ArrayList;
import java.util.List;

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
	private List<String> hobbys;
	private List<String> images;
}
