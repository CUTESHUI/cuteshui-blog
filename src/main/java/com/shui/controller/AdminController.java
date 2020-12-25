package com.shui.controller;

import com.shui.common.lang.Result;
import com.shui.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/jie-set")
    public Result jetSet(Long id, Integer rank, String field) {
        return adminService.jetSet(id, rank, field);
    }

    @PostMapping("/initEsData")
    public Result initEsData() {
        return adminService.initEsData();
    }


}
