package com.gitee.freakchicken.dbapi.basic.service.impl;

import com.gitee.freakchicken.dbapi.basic.domain.DataSource;
import com.gitee.freakchicken.dbapi.basic.mapper.ApiConfigMapper;
import com.gitee.freakchicken.dbapi.basic.mapper.DataSourceMapper;
import com.gitee.freakchicken.dbapi.basic.service.IDataSourceService;
import com.gitee.freakchicken.dbapi.basic.service.IMetaDataCacheManager;
import com.gitee.freakchicken.dbapi.basic.util.DESUtils;
import com.gitee.freakchicken.dbapi.basic.util.PoolManager;
import com.gitee.freakchicken.dbapi.basic.util.UUIDUtil;
import com.gitee.freakchicken.dbapi.dto.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: dbApi
 * @description:
 * @author:
 * @create: 2021-01-20 10:43
 **/
@Slf4j
@Service
public class DataSourceServiceImpl implements IDataSourceService {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private IMetaDataCacheManager iMetaDataCacheManager;

    @Autowired
    private DataSourceMapper dataSourceMapper;

    @Autowired
    private ApiConfigMapper apiConfigMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(DataSource dataSource) {
        dataSource.setId(UUIDUtil.id());
        dataSource.setUpdateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        dataSource.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        //新增数据源对密码加密
        try {
            dataSource.setPassword(DESUtils.encrypt(dataSource.getPassword()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        dataSourceMapper.insert(dataSource);
    }

    //    @CacheEvict(value = "datasource", key = "#dataSource.id")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DataSource dataSource) {
        dataSource.setUpdateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        //如果修改了密码, 需要对密码加密
        if (dataSource.isEdit_password()) {
            try {
                dataSource.setPassword(DESUtils.encrypt(dataSource.getPassword()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        dataSourceMapper.updateById(dataSource);
        PoolManager.removeJdbcConnectionPool(dataSource.getId());
        cacheManager.getCache("datasource").evictIfPresent(dataSource.getId());

        //如果是集群模式，清除每个apiServer节点内的元数据ehcache缓存
        iMetaDataCacheManager.cleanDatasourceMetaCacheIfCluster(dataSource.getId());
    }

    //    @CacheEvict(value = "datasource", key = "#id")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO delete(String id) {
        int i = apiConfigMapper.countByDatasoure(id);
        if (i == 0) {
            dataSourceMapper.deleteById(id);

            PoolManager.removeJdbcConnectionPool(id);
            cacheManager.getCache("datasource").evictIfPresent(id);

            //如果是集群模式，清除每个apiServer节点内的元数据ehcache缓存

            iMetaDataCacheManager.cleanDatasourceMetaCacheIfCluster(id);
            return ResponseDTO.successWithMsg("delete success");
        } else {
            return ResponseDTO.fail("datasource has been used, can not delete");
        }
    }

    @Override
    @Cacheable(value = "datasource", key = "#id", unless = "#result == null")
    public DataSource detail(String id) {
        DataSource dataSource = dataSourceMapper.selectById(id);
        return dataSource;
    }

    @Override
    public List<DataSource> getAll() {
        List<DataSource> list = dataSourceMapper.selectList(null);
        List<DataSource> collect = list.stream().sorted(Comparator.comparing(DataSource::getUpdateTime).reversed()).collect(Collectors.toList());
        return collect;
    }

    @Override
    public String getDBType(Integer id) {
        return dataSourceMapper.selectById(id).getType();
    }

    @Override
    public List<DataSource> selectBatch(List<String> ids) {
        List<DataSource> dataSources = dataSourceMapper.selectBatchIds(ids);
        return dataSources;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertBatch(List<DataSource> list) {
        list.forEach(t -> {
            t.setUpdateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            dataSourceMapper.insert(t);
        });
    }
}
