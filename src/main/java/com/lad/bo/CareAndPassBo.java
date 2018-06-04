package com.lad.bo;

import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Document(collection = "careAndPass")
public class CareAndPassBo extends BaseBo {
	// 责任主体id
	private String mainId;
	
	// 被动实体id集合
	private Map<String,List<String>> roster;
	
	// 拉黑还是关注  0. 拉黑 ; 1. 关注
	private String type;
	
	// 是什么情境下进行的拉黑 1. 找儿媳;2. 找老伴;3. 找驴友
	private String situation;
}
