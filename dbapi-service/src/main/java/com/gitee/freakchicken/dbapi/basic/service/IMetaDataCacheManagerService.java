package com.gitee.freakchicken.dbapi.basic.service;

public interface IMetaDataCacheManagerService {

    void cleanApiMetaCacheIfCluster(String key);

    void cleanDatasourceMetaCacheIfCluster(String id);

    void cleanTokenAuthMetaCacheIfCluster(String appId);

    void cleanTokenMetaCacheIfCluster(String tokenId);

    void gatewayIPRuleCacheSyncIfCluster();

}
