package com.gitee.freakchicken.dbapi.constant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * @description: 运行模式
 * @program: dbApi
 * @author: kensan
 * @create: 2022-09-11 16:49
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ModeConstant {
    public static final String CLUSTER = "cluster";
    public static final String STANDALONE = "standalone";
}
