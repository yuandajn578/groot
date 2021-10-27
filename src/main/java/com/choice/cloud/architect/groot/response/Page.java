package com.choice.cloud.architect.groot.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.ToString;

/**
 * @author yuxiaopeng
 * @version v1.0
 * @description
 * @createTime 2018-10-15 18:51
 * @email
 */
@ToString
public class Page {

    private Integer pageNum = 1;

    private Integer pageSize = 20;

    private Integer total;

    private Integer currentCount;

    @JsonIgnore
    private Integer begin;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum <1?1:pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize <1?20:pageSize;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(Integer currentCount) {
        this.currentCount = currentCount;
    }

    public Integer getBegin() {
        return (pageNum-1)*pageSize;
    }

    public void initPage(){
        this.pageNum = this.pageNum < 1 ? 1 : this.pageNum;
        this.pageSize = this.pageSize < 1 ? 20 : this.pageSize;
    }
}
