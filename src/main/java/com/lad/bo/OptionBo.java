package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Document(collection = "options")
public class OptionBo extends BaseBo{
	private String value;	
	private String field;	// 属于哪一个字段
	private String supId;	// 上级id(可选,涉及多级联动的选项需要填写该字段)
	private String template;// 使用模板(涉及范围选项需要填写该字段)
	private int sort;	// 排序
	private int status; // 状态,是否启用
}
