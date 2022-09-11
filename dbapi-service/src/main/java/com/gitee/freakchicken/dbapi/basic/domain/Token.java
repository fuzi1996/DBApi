package com.gitee.freakchicken.dbapi.basic.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TableName(value = "token")
public class Token {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField
    private String token;

    @TableField
    private Long expire;

    @TableField
    private String note;

    @TableField("create_time")
    private Long createTime;

}
