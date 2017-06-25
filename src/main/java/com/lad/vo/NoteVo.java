package com.lad.vo;

/**
 * 功能描述：
 * Version: 1.0
 * Time:2017/6/25
 */
public class NoteVo extends BaseVo {

    private String nodeid;

    private String subject;

    private String content;

    private Long visitCount;

    private Long commontCount;

    private Long transCount;

    private String createuid;

    private boolean isForward = false;

    public String getNodeid() {
        return nodeid;
    }

    public void setNodeid(String nodeid) {
        this.nodeid = nodeid;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(Long visitCount) {
        this.visitCount = visitCount;
    }

    public Long getCommontCount() {
        return commontCount;
    }

    public void setCommontCount(Long commontCount) {
        this.commontCount = commontCount;
    }

    public Long getTransCount() {
        return transCount;
    }

    public void setTransCount(Long transCount) {
        this.transCount = transCount;
    }

    public String getCreateuid() {
        return createuid;
    }

    public void setCreateuid(String createuid) {
        this.createuid = createuid;
    }

    public boolean isForward() {
        return isForward;
    }

    public void setForward(boolean forward) {
        isForward = forward;
    }
}
