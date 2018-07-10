package com.lad.bo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Document(collection="oldFriendRequire")
public class OldFriendRequireBo extends BaseBo {
	private String sex;
	private String age;
	private String address;
	private Map<String,Set<String>> hobbys = new HashMap<>();// 兴趣,list
	private List<String> images = new ArrayList<>();
	private boolean agree = false;
}
