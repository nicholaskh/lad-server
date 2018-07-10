package com.lad.dao;

import com.lad.bo.ReadHistoryBo;

public interface IReadHistoryDao extends IBaseDao {

	String addReadHistory(ReadHistoryBo historyBo);

	ReadHistoryBo getHistoryByUseridAndNoteId(String userid, String id);

	String updateReadNum(String id,int readNum);

}
