package com.shui.entity;

import lombok.Data;

import java.util.Date;

/**
 * 基础Entity，所有实体类必须继承
 * @author CUTESHUI
 * @since 2020-09-24
 */
@Data
public class BaseEntity {

    private Long id;
    private Date created;
    private Date modified;
}
