package com.lad.scrapybo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 功能描述：广播
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/29
 */
@Getter
@Setter
@Document(collection = "broadcast")
public class BroadcastBo extends BaseInforBo {

    private String play_times;

    private String intro;

    private String broadcast_url;

    private int random_num;

    private String edition;
    //合集总数量
    private int totalNum;

}
