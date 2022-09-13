package com.gitee.freakchicken.dbapi.basic.service.impl;

import com.gitee.freakchicken.dbapi.basic.service.ICacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @program: dbApi
 * @author: kensan
 * @create: 2022-09-13 21:43
 */
@Service
public class CacheServiceImpl implements ICacheService {

		@Autowired
		private CacheManager cacheManager;

		@Override
		public boolean evictIfPresent(String catalog, Object key) {
				Cache cache = this.cacheManager.getCache(catalog);
				if (Objects.nonNull(cache)) {
						return cache.evictIfPresent(key);
				}
				return false;
		}

		@Override
		public Cache.ValueWrapper putIfAbsent(String catalog, Object key, Object value) {
				Cache cache = this.cacheManager.getCache(catalog);
				if (Objects.nonNull(cache)) {
						return cache.putIfAbsent(key, value);
				}
				return null;
		}

		@Override
		public <T> T get(String catalog, Object key, Class<T> clazz) {
				Cache cache = this.cacheManager.getCache(catalog);
				if (Objects.nonNull(cache)) {
						return cache.get(key, clazz);
				}
				return null;
		}
}
