package com.gitee.freakchicken.dbapi.basic.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: dbApi
 * @description:
 * @author: jiangqiang
 * @create: 2021-01-20 09:52
 **/
@Data
@NoArgsConstructor
@TableName(value = "datasource")
public class DataSource {

    @TableId(value = "id")
    private String id;

    @TableField
    private String name;

    @TableField
    private String note;

    @TableField
    private String url;

    @TableField
    private String username;

    @TableField
    private String password;

    /**
     * true 修改密码 false不修改
     */
    @TableField(exist = false)
    private boolean edit_password;

    @TableField
    private String type;

    @TableField
    private String driver;

    @TableField(value = "table_sql")
    private String tableSql;

    @TableField(value = "create_time")
    private String createTime;

    @TableField(value = "update_time")
    private String updateTime;

}
