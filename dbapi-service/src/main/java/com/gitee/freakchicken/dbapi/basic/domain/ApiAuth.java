package com.gitee.freakchicken.dbapi.basic.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TableName(value = "api_auth")
public class ApiAuth {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("app_id")
    private String appId;

    @TableField("group_id")
    private String groupId;
}
