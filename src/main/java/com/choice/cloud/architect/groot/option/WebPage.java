package com.choice.cloud.architect.groot.option;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

public class WebPage<T> {

    @NotNull
    private Integer pageNum;

    @NotNull
    private Integer pageSize;

    private Long total;

    private T data;

    private Map<String,String> orderBy;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Map<String, String> getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(Map<String, String> orderBy) {
        this.orderBy = orderBy;
    }

    public WebPage() {

    }
    public static WebPage empty() {
        return new WebPage();
    }
    public WebPage(Integer pageNum, Integer pageSize, long total, T data) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.data = data;
    }

    public static <V> List<V> pageingList(List<V> list, Integer pageNum, Integer pageSize) {
        if (null == pageNum || null == pageSize) {
            pageNum = 1;
            pageSize = 10;
        }

        int from = (pageNum - 1) * pageSize;
        int to = Math.min(pageNum * pageSize, list.size());
        if (from > to) {
            from = to;
        }
        return list.subList(from, to);
    }
}
