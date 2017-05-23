package com.lad.service;

import java.util.List;

import com.lad.bo.TempFaceToFaceChatroomBo;
import com.mongodb.WriteResult;

public interface ITempFaceToFaceChatroomService extends IBaseService {
	
	public TempFaceToFaceChatroomBo insert(TempFaceToFaceChatroomBo tempFaceToFaceChatroomBo);
	public TempFaceToFaceChatroomBo selectByUserid(String userid);
	public List<TempFaceToFaceChatroomBo> selectBySeq(String seq);
	public long countBySeq(String seq);
	public WriteResult deleteByUserid(String userid);
	public WriteResult deleteBySeq(String seq);

}
