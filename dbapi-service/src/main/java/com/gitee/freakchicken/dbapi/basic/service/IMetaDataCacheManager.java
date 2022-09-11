package com.gitee.freakchicken.dbapi.basic.service;

public interface IMetaDataCacheManager {

    void cleanApiMetaCacheIfCluster(String key);

    void cleanDatasourceMetaCacheIfCluster(String id);

    void cleanTokenAuthMetaCacheIfCluster(String appId);

    void cleanTokenMetaCacheIfCluster(String tokenId);

    void gatewayIPRuleCacheSyncIfCluster();

}
