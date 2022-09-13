package com.gitee.freakchicken.dbapi.basic.service;

import org.springframework.cache.Cache;

/**
 * @program: dbApi
 * @author: kensan
 * @create: 2022-09-13 21:43
 */
public interface ICacheService {
		boolean evictIfPresent(String catalog, Object key);

		Cache.ValueWrapper putIfAbsent(String catalog, Object key, Object value);

		<T> T get(String catalog, Object key, Class<T> clazz);
}
