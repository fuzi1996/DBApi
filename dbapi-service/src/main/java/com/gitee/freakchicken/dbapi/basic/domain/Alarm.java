package com.gitee.freakchicken.dbapi.basic.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TableName(value = "api_alarm")
public class Alarm {

    @TableField(value = "api_id")
    private String apiId;

    @TableField("alarm_plugin")
    private String alarmPlugin;

    @TableField("alarm_plugin_param")
    private String alarmPluginParam;
}