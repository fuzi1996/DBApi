package com.gitee.freakchicken.dbapi.basic.service.impl;

import com.gitee.freakchicken.dbapi.basic.domain.ApiAuth;
import com.gitee.freakchicken.dbapi.basic.domain.AppInfo;
import com.gitee.freakchicken.dbapi.basic.mapper.ApiAuthMapper;
import com.gitee.freakchicken.dbapi.basic.mapper.AppInfoMapper;
import com.gitee.freakchicken.dbapi.basic.service.IAppService;
import com.gitee.freakchicken.dbapi.basic.service.IMetaDataCacheManagerService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class AppServiceImpl implements IAppService {

		@Autowired
		private AppInfoMapper appInfoMapper;
		@Autowired
		private ApiAuthMapper apiAuthMapper;

		@Autowired
		private IMetaDataCacheManagerService iMetaDataCacheManagerService;

		@Override
		@Transactional(rollbackFor = Exception.class)
		public AppInfo add(AppInfo app) {
				app.setId(RandomStringUtils.random(16, true, true));
				app.setSecret(RandomStringUtils.random(32, true, true));
				if (app.getExpireDesc().equals("5min")) {
						app.setExpireTime(5 * 60);
				} else if (app.getExpireDesc().equals("1hour")) {
						app.setExpireTime(60 * 60);
				} else if (app.getExpireDesc().equals("1day")) {
						app.setExpireTime(60 * 60 * 24);
				} else if (app.getExpireDesc().equals("30day")) {
						app.setExpireTime(60 * 60 * 24 * 30);
				} else if (app.getExpireDesc().equals("单次有效")) {
						app.setExpireTime(0);
				} else if (app.getExpireDesc().equals("永久有效")) {
						app.setExpireTime(-1);
				}

				appInfoMapper.insert(app);
				return app;
		}

		@Override
		public List<AppInfo> getAll() {
				return appInfoMapper.selectByMap(null);
		}

		@Override
		@Transactional(rollbackFor = Exception.class)
		public void delete(String appid) {
				appInfoMapper.deleteById(appid);
		}

		@Override
		@Transactional(rollbackFor = Exception.class)
		@CacheEvict(value = "app_AuthGroups", key = "#appId")
		public void auth(String appId, String groupIds) {
				apiAuthMapper.deleteByAppId(appId);
				if (StringUtils.isNoneBlank(groupIds)) {
						String[] split = groupIds.split(",");
						Arrays.stream(split).forEach(t -> {
								ApiAuth auth = new ApiAuth();
								auth.setAppId(appId);
								auth.setGroupId(t);
								apiAuthMapper.insert(auth);
						});
				}
				iMetaDataCacheManagerService.cleanTokenAuthMetaCacheIfCluster(appId);
		}

		@Override
		@Cacheable(value = "app_AuthGroups", key = "#appId", unless = "#result == null")
		public List<String> getAuthGroups(String appId) {
				List<String> list = apiAuthMapper.selectByAppId(appId);
				return list;
		}
}