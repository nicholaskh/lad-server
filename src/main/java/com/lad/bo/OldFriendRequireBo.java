package com.lad.bo;

import java.util.ArrayList;
import java.util.List;

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
	private List<String> hobbys = new ArrayList<>();
	private List<String> images = new ArrayList<>();
	private boolean agree = false;
}
