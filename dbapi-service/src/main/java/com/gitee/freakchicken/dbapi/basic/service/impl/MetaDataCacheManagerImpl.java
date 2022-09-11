package com.gitee.freakchicken.dbapi.basic.service.impl;

import com.gitee.freakchicken.dbapi.basic.service.IMetaDataCacheManager;
import com.gitee.freakchicken.dbapi.constant.ModeConstant;
import com.gitee.freakchicken.dbapi.dto.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Slf4j
public class MetaDataCacheManagerImpl implements IMetaDataCacheManager {

    @Value("${dbapi.cluster.api.name}")
    private String apiName;

    @Value("${dbapi.cluster.gateway.name}")
    private String gatewayName;

    @Value("${dbapi.mode:cluster}")
    private String mode;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Override
    public void cleanApiMetaCacheIfCluster(String key) {
        try {
            if (ModeConstant.CLUSTER.equals(mode)) {
                RestTemplate restTemplate = new RestTemplate();
                List<ServiceInstance> instances = discoveryClient.getInstances(apiName);

                for (ServiceInstance instance : instances) {
                    String url = String.format("http://%s:%s/metacache/clean/api?key=%s", instance.getHost(), instance.getPort(), key);
                    restTemplate.getForEntity(url, ResponseDTO.class);
                    log.info("api meta cache clean to node: {}", instance.getHost());
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void cleanDatasourceMetaCacheIfCluster(String id) {
        try {
            if (ModeConstant.CLUSTER.equals(mode)) {
                RestTemplate restTemplate = new RestTemplate();
                List<ServiceInstance> instances = discoveryClient.getInstances(apiName);

                for (ServiceInstance instance : instances) {
                    String url = String.format("http://%s:%s/metacache/clean/datasource?id=%s", instance.getHost(), instance.getPort(), id);
                    restTemplate.getForEntity(url, ResponseDTO.class);
                    log.info("datasource meta cache clean to node: {}", instance.getHost());
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void cleanTokenAuthMetaCacheIfCluster(String appId) {
        try {
            if (ModeConstant.CLUSTER.equals(mode)) {
                RestTemplate restTemplate = new RestTemplate();
                List<ServiceInstance> instances = discoveryClient.getInstances(apiName);

                for (ServiceInstance instance : instances) {
                    String url = String.format("http://%s:%s/metacache/clean/tokenAuth?id=%s", instance.getHost(), instance.getPort(), appId);
                    restTemplate.getForEntity(url, ResponseDTO.class);
                    log.info("token auth meta cache clean to node: {}", instance.getHost());
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void cleanTokenMetaCacheIfCluster(String tokenId) {
        try {
            if (ModeConstant.CLUSTER.equals(mode)) {
                RestTemplate restTemplate = new RestTemplate();
                List<ServiceInstance> instances = discoveryClient.getInstances(apiName);

                for (ServiceInstance instance : instances) {
                    String url = String.format("http://%s:%s/metacache/clean/token?id=%s", instance.getHost(), instance.getPort(), tokenId);
                    restTemplate.getForEntity(url, ResponseDTO.class);
                    log.info("token meta cache clean to node: {}", instance.getHost());
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void gatewayIPRuleCacheSyncIfCluster() {

        if (ModeConstant.CLUSTER.equals(mode)) {
            RestTemplate restTemplate = new RestTemplate();
            List<ServiceInstance> instances = discoveryClient.getInstances(gatewayName);

            for (ServiceInstance instance : instances) {
                String url = String.format("http://%s:%s/metacache/iprule/sync", instance.getHost(), instance.getPort());
                restTemplate.getForEntity(url, ResponseDTO.class);
                log.info("sync ip rule cache to gateway node: {}", instance.getHost());
            }
        }

    }

}
