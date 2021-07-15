package com.example.mall.common;

//分页的页面参数

import lombok.Data;

public class PageAndLimit {
    private int limit;
    private int page;

//    public PageAndLimit(){
//        this.limit = 10;
//        this.page = 1;
//    }

    public int getLimit() {
        return this.limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
