package com.gitee.freakchicken.dbapi.basic.constant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * @program: dbApi
 * @author: kensan
 * @create: 2022-09-13 21:30
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CacheKeyConstant {
		public static String EHCACHE_APP_TOKEN = "app_token";
		public static String EHCACHE_TOKEN_APP = "token_app";
		public static String EHCACHE_APP_AUTH_GROUPS = "app_AuthGroups";
}
