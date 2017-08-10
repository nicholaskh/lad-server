package com.lad.vo;

/**
 * 功能描述：
 * Version: 1.0
 * Time:2017/6/29
 */
public class UserStarVo extends UserBaseVo{

    private Long totalCount;

    private Long weekCount;

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getWeekCount() {
        return weekCount;
    }

    public void setWeekCount(Long weekCount) {
        this.weekCount = weekCount;
    }
}
