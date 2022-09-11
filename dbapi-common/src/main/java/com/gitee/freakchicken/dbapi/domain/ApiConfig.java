package com.gitee.freakchicken.dbapi.domain;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: dbApi
 * @description:
 * @author: jiangqiang
 * @create: 2021-01-20 09:50
 **/
@Data
@NoArgsConstructor
@TableName(value = "api_config")
public class ApiConfig {

    @TableId(value = "id")
    private String id;

    @TableField
    private String name;

    @TableField
    private String note;

    @TableField
    private String path;

    @TableField(value = "datasource_id")
    private String datasourceId;

    @TableField(exist = false)
    private List<ApiSql> sqlList;

    /**
     * application/x-www-form-urlencoded 类API对应的参数
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String params;

    @TableField
    private Integer status;

    @TableField
    private Integer previlege;

    @TableField("group_id")
    private String groupId;

    @TableField(value = "cache_plugin", insertStrategy = FieldStrategy.NOT_EMPTY)
    private String cachePlugin;

    /**
     * 缓存插件参数
     */
    @TableField(value = "cache_plugin_params", updateStrategy = FieldStrategy.IGNORED)
    private String cachePluginParams;

    @TableField(value = "create_time")
    private String createTime;

    @TableField(value = "update_time")
    private String updateTime;

    @TableField(value = "content_type")
    private String contentType;

    /**
     * 是否打开事务，1-是；0-否
     */
    @TableField("open_trans")
    private Integer openTrans;

    /**
     * application/json 类API对应的json参数示例
     */
    @TableField(value = "json_param", updateStrategy = FieldStrategy.IGNORED)
    private String jsonParam;

    @TableField(exist = false)
    private String alarmPlugin;

    @TableField(exist = false)
    private String alarmPluginParam;
}
