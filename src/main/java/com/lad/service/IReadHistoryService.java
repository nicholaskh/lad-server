package com.lad.service;

import com.lad.bo.ReadHistoryBo;

public interface IReadHistoryService extends IBaseService {

	String addReadHistory(ReadHistoryBo historyBo);

	ReadHistoryBo getHistoryByUseridAndNoteId(String userid, String id);

	String updateReadNum(String id,int readNum);

}
