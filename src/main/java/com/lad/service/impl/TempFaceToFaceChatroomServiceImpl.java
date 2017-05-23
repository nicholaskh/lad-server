package com.lad.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lad.bo.TempFaceToFaceChatroomBo;
import com.lad.dao.ITempFaceToFaceChatroomDao;
import com.lad.service.ITempFaceToFaceChatroomService;
import com.mongodb.WriteResult;

@Service("tempFaceToFaceChatroomService")
public class TempFaceToFaceChatroomServiceImpl implements
		ITempFaceToFaceChatroomService {

	@Autowired
	private ITempFaceToFaceChatroomDao tempFaceToFaceChatroomDao;
	
	public TempFaceToFaceChatroomBo insert(
			TempFaceToFaceChatroomBo tempFaceToFaceChatroomBo) {
		return tempFaceToFaceChatroomDao.insert(tempFaceToFaceChatroomBo);
	}

	public TempFaceToFaceChatroomBo selectByUserid(String userid) {
		return tempFaceToFaceChatroomDao.selectByUserid(userid);
	}

	public List<TempFaceToFaceChatroomBo> selectBySeq(String seq) {
		return tempFaceToFaceChatroomDao.selectBySeq(seq);
	}

	public WriteResult deleteByUserid(String userid) {
		return tempFaceToFaceChatroomDao.deleteByUserid(userid);
	}

	public WriteResult deleteBySeq(String seq) {
		return tempFaceToFaceChatroomDao.deleteBySeq(seq);
	}

	public long countBySeq(String seq) {
		return tempFaceToFaceChatroomDao.countBySeq(seq);
	}

}
