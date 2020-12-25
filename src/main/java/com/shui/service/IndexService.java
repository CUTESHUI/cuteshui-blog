package com.shui.service;

public interface IndexService {

    /**
     * 首页地址
     */
    String index();

    /**
     *  ES的搜索功能地址
     *  q：关键字
     */
    String search(String q);
}
