package com.gitee.freakchicken.dbapi.apiserver.controller;

import com.gitee.freakchicken.dbapi.basic.util.PoolManager;
import com.gitee.freakchicken.dbapi.dto.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 集群模式的ehcache多节点同步
 */
@Slf4j
@RestController
@RequestMapping("/metacache/clean")
public class MetaCacheController {

    @Autowired
    private CacheManager cacheManager;

    @RequestMapping("/api")
    public ResponseDTO cleanCache(String key) {
        log.info("clean api cache when cluster");
        cacheManager.getCache("api").evictIfPresent(key);
        return ResponseDTO.successWithData(null);
    }

    @RequestMapping("/datasource")
    public ResponseDTO cleanDatasourceCache(String id) {
        log.info("clean datasource cache when cluster");
        cacheManager.getCache("datasource").evictIfPresent(id);
        PoolManager.removeJdbcConnectionPool(id);
        return ResponseDTO.successWithData(null);
    }

    @RequestMapping("/tokenAuth")
    public ResponseDTO cleanTokenAuthCache(String id) {
        log.info("clean token auth cache when cluster");
        cacheManager.getCache("token_AuthGroups").evictIfPresent(id);
        return ResponseDTO.successWithData(null);
    }

    @RequestMapping("/token")
    public ResponseDTO cleanTokenCache(String id) {
        log.info("clean token cache when cluster");
        cacheManager.getCache("token").evictIfPresent(id);
        return ResponseDTO.successWithData(null);
    }

}
