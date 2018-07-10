package com.lad.bo;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

import com.lad.util.CommonUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Document(collection="readHistory")
@Setter
@Getter
@ToString
public class ReadHistoryBo extends BaseBo{
	private String readerId;
	private String beReaderId;
	// 0 帖子访问		1 资讯访问	5 帖子评论访问， 6 资讯评论访问	
	private int type;
	private int readNum;
	private Date date = CommonUtil.getZeroDate(new Date());
}
