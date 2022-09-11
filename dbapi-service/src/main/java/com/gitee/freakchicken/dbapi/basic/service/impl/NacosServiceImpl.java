package com.gitee.freakchicken.dbapi.basic.service.impl;

import com.gitee.freakchicken.dbapi.basic.service.INacosService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
public class NacosServiceImpl implements INacosService {

    @Value("${dbapi.cluster.gateway.name}")
    private String gatewayName;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Override
    public String getGatewayAddress() {
        List<ServiceInstance> instances = discoveryClient.getInstances(gatewayName);
        return instances.get(0).getHost() + ":" + instances.get(0).getPort();
    }
}
