package com.gitee.freakchicken.dbapi.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TableName(value = "api_sql")
public class ApiSql {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("api_id")
    private String apiId;

    @TableField("sql_text")
    private String sqlText;

    @TableField(value = "transform_plugin", insertStrategy = FieldStrategy.NOT_EMPTY)
    private String transformPlugin;

    @TableField(value = "transform_plugin_params")
    private String transformPluginParams;

    public ApiSql(String apiId, String sql) {
        this.apiId = apiId;
        this.sqlText = sql;
    }
}
