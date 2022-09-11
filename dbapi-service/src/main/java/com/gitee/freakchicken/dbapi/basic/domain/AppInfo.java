package com.gitee.freakchicken.dbapi.basic.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TableName("app_info")
public class AppInfo {
    @TableId()
    private String id;

    @TableField()
    private String secret;

    @TableField()
    private String name;

    @TableField()
    private String note;

    @TableField()
    private String expireDesc;

    @TableField()
    private long expireTime; // -1永久；0 单次失效；> 0 失效时间
}