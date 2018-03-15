package com.lad.vo;

import java.util.Date;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2018
 * Version: 1.0
 * Time:2018/3/15
 */
public class UserShowVo extends UserBaseVo {

    private Date createTime;
    //是否是点赞
    private boolean thumbsup;

    private String comment;

    private String notePic;

    private String noteContent;

    private String noteVideo;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public boolean isThumbsup() {
        return thumbsup;
    }

    public void setThumbsup(boolean thumbsup) {
        this.thumbsup = thumbsup;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getNotePic() {
        return notePic;
    }

    public void setNotePic(String notePic) {
        this.notePic = notePic;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public String getNoteVideo() {
        return noteVideo;
    }

    public void setNoteVideo(String noteVideo) {
        this.noteVideo = noteVideo;
    }
}
