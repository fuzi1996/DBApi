package com.gitee.freakchicken.dbapi.basic.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class AccessLog {
    private String url;
    private String status;
    private int duration;
    private Date startTime;
    private String ip;
    private String clientId;
    private String apiId;
}