package com.shui.service;

import com.shui.common.lang.Result;

public interface AdminService {

    /**
     * 后台管理员手动全量同步到es，否则没有信息在es中，也就搜索不了
     */
    Result initEsData();

    /**
     * rank：0表示取消，1表示
     */
    Result jetSet(Long id, Integer rank, String field);
}
