package com.gitee.freakchicken.dbapi.basic.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gitee.freakchicken.dbapi.basic.domain.Alarm;
import com.gitee.freakchicken.dbapi.basic.domain.ApiDTO;
import com.gitee.freakchicken.dbapi.basic.mapper.AlarmMapper;
import com.gitee.freakchicken.dbapi.basic.mapper.ApiConfigMapper;
import com.gitee.freakchicken.dbapi.basic.mapper.ApiSqlMapper;
import com.gitee.freakchicken.dbapi.basic.mapper.DataSourceMapper;
import com.gitee.freakchicken.dbapi.basic.service.IApiConfigService;
import com.gitee.freakchicken.dbapi.basic.util.UUIDUtil;
import com.gitee.freakchicken.dbapi.domain.ApiConfig;
import com.gitee.freakchicken.dbapi.domain.ApiSql;
import com.gitee.freakchicken.dbapi.dto.ResponseDTO;
import com.gitee.freakchicken.dbapi.plugin.CachePlugin;
import com.gitee.freakchicken.dbapi.plugin.PluginManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: dbApi
 * @description:
 * @author: jiangqiang
 * @create: 2021-01-19 17:27
 **/
@Slf4j
@Service
public class ApiConfigServiceImpl implements IApiConfigService {

    @Autowired
    private ApiConfigMapper apiConfigMapper;
    @Autowired
    private DataSourceMapper dataSourceMapper;
    @Autowired
    private ApiSqlMapper apiSqlMapper;
    @Autowired
    private AlarmMapper alarmMapper;
    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private com.gitee.freakchicken.dbapi.basic.service.IMetaDataCacheManager IMetaDataCacheManager;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO add(ApiConfig apiConfig) {

        int size = apiConfigMapper.selectCountByPath(apiConfig.getPath());
        if (size > 0) {
            return ResponseDTO.fail("Path has been used!");
        } else {
            apiConfig.setStatus(0);
            String id = UUIDUtil.id();
            apiConfig.setId(id);

            if (MediaType.APPLICATION_JSON_VALUE.equals(apiConfig.getContentType())) {
                apiConfig.setParams("[]"); //不能设置null 前端使用会报错
            } else if (MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(apiConfig.getContentType())) {
                apiConfig.setJsonParam(null);
            }

            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            apiConfig.setCreateTime(now);
            apiConfig.setUpdateTime(now);

            apiConfigMapper.insert(apiConfig);

            apiConfig.getSqlList().stream().forEach(t -> {
                t.setApiId(apiConfig.getId());
                apiSqlMapper.insert(t);
            });

            if (StringUtils.isNoneBlank(apiConfig.getAlarmPlugin())) {
                Alarm alarm = new Alarm();
                alarm.setApiId(id);
                alarm.setAlarmPlugin(apiConfig.getAlarmPlugin());
                alarm.setAlarmPluginParam(apiConfig.getAlarmPluginParam());
                alarmMapper.insert(alarm);
            }
            return ResponseDTO.successWithMsg("create API success");
        }

    }

    //    @CacheEvict(value = "api", key = "#apiConfig.path")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO update(ApiConfig apiConfig) {

        int size = apiConfigMapper.selectCountByPathWhenUpdate(apiConfig.getPath(), apiConfig.getId());
        if (size > 0) {
            return ResponseDTO.fail("Path has been used");
        } else {
            ApiConfig oldConfig = apiConfigMapper.selectById(apiConfig.getId());
            apiConfig.setStatus(0);
            apiConfig.setUpdateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

            if (MediaType.APPLICATION_JSON_VALUE.equals(apiConfig.getContentType())) {
                apiConfig.setParams("[]"); //不能设置null 前端使用会报错
            } else if (MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(apiConfig.getContentType())) {
                apiConfig.setJsonParam(null);
            }

            apiConfigMapper.updateById(apiConfig);

            apiSqlMapper.deleteByApiID(apiConfig.getId());
            apiConfig.getSqlList().stream().forEach(t -> {
                t.setApiId(apiConfig.getId());
                apiSqlMapper.insert(t);
            });

            alarmMapper.deleteByApiID(apiConfig.getId());
            if (StringUtils.isNoneBlank(apiConfig.getAlarmPlugin())) {
                Alarm alarm = new Alarm();
                alarm.setApiId(apiConfig.getId());
                alarm.setAlarmPlugin(apiConfig.getAlarmPlugin());
                alarm.setAlarmPluginParam(apiConfig.getAlarmPluginParam());
                alarmMapper.insert(alarm);
            }

            //清除缓存插件对应的所有缓存
            if (StringUtils.isNoneBlank(oldConfig.getCachePlugin())) {
                try {
                    CachePlugin cachePlugin = PluginManager.getCachePlugin(oldConfig.getCachePlugin());
                    cachePlugin.clean(oldConfig);
                    log.debug("clean cache from old config when update api");
                } catch (Exception e) {
                    log.error("clean cache failed when update api", e);
                }
            }

            cacheManager.getCache("api").evictIfPresent(apiConfig.getPath());
            //如果是集群模式，清除每个apiServer节点内的元数据ehcache缓存
            IMetaDataCacheManager.cleanApiMetaCacheIfCluster(apiConfig.getPath());

            return ResponseDTO.successWithMsg("update API success");
        }

    }

    //    @CacheEvict(value = "api", key = "#path")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        ApiConfig oldConfig = apiConfigMapper.selectById(id);
        apiConfigMapper.deleteById(id);
        apiSqlMapper.deleteByApiID(id);
        alarmMapper.deleteByApiID(id);

        //清除所有缓存
        if (StringUtils.isNoneBlank(oldConfig.getCachePlugin())) {
            try {
                CachePlugin cachePlugin = PluginManager.getCachePlugin(oldConfig.getCachePlugin());
                cachePlugin.clean(oldConfig);
                log.debug("delete api then clean cache");
            } catch (Exception e) {
                log.error("clean cache failed when delete api", e);
            }
        }
        cacheManager.getCache("api").evictIfPresent(oldConfig.getPath());
        //如果是集群模式，清除每个apiServer节点内的元数据ehcache缓存
        IMetaDataCacheManager.cleanApiMetaCacheIfCluster(oldConfig.getPath());
    }

    @Override
    public ApiConfig detail(String id) {
        ApiConfig apiConfig = apiConfigMapper.selectById(id);
        List<ApiSql> list = apiSqlMapper.selectByApiId(apiConfig.getId());
        apiConfig.setSqlList(list);
        List<Alarm> alarms = alarmMapper.selectByApiId(apiConfig.getId());
        if (alarms.size() > 0) {
            apiConfig.setAlarmPlugin(alarms.get(0).getAlarmPlugin());
            apiConfig.setAlarmPluginParam(alarms.get(0).getAlarmPluginParam());
        }
        return apiConfig;
    }

    @Override
    public List<ApiConfig> getAll() {
        List<ApiConfig> list = apiConfigMapper.selectList(null);
        List<ApiConfig> collect = list.stream().sorted(Comparator.comparing(ApiConfig::getUpdateTime).reversed()).collect(Collectors.toList());
        return collect;
    }

    @Override
    public JSONArray getAllDetail() {
        List<ApiDTO> list = apiConfigMapper.getAllDetail();

        Map<String, List<ApiDTO>> map = list.stream().collect(Collectors.groupingBy(ApiDTO::getGroupName));

        JSONArray array = new JSONArray();
        map.keySet().forEach(t -> {
            JSONObject jo = new JSONObject();
            jo.put("name", t);
            List<ApiDTO> apiDTOS = map.get(t);
            jo.put("children", apiDTOS);
            array.add(jo);
        });
        return array;

    }

    @Override
    public List<ApiConfig> search(String keyword, String field, String groupId) {
        if (StringUtils.isNoneBlank(keyword)) {
            keyword = "%" + keyword + "%";
        }
        return apiConfigMapper.selectByKeyword(keyword, field, groupId);
    }

    /**
     * servlet 从这获取API元数据
     */
    @Override
    @Cacheable(value = "api", key = "#path", unless = "#result == null")
    public ApiConfig getConfig(String path) {
        log.info("get [{}] api config from db", path);
        ApiConfig apiConfig = apiConfigMapper.selectByPathOnline(path);
        if (Objects.isNull(apiConfig)) {
            log.warn("can't get [{}] api config from db", path);
            return null;
        }
        List<ApiSql> apiSqls = apiSqlMapper.selectByApiId(apiConfig.getId());
        apiConfig.setSqlList(apiSqls);
        List<Alarm> alarms = alarmMapper.selectByApiId(apiConfig.getId());
        if (alarms.size() > 0) {
            apiConfig.setAlarmPlugin(alarms.get(0).getAlarmPlugin());
            apiConfig.setAlarmPluginParam(alarms.get(0).getAlarmPluginParam());
        }
        return apiConfig;
    }

    @Override
    public void online(String id, String path) {
        ApiConfig apiConfig = apiConfigMapper.selectById(id);
        apiConfig.setStatus(1);
        apiConfigMapper.updateById(apiConfig);
    }

    //    @CacheEvict(value = "api", key = "#path")
    @Override
    public void offline(String id, String path) {

        ApiConfig apiConfig = apiConfigMapper.selectById(id);
        apiConfig.setStatus(0);
        apiConfigMapper.updateById(apiConfig);

        cacheManager.getCache("api").evictIfPresent(path);

        if (StringUtils.isNoneBlank(apiConfig.getCachePlugin())) {
            try {
                CachePlugin cachePlugin = PluginManager.getCachePlugin(apiConfig.getCachePlugin());
                cachePlugin.clean(apiConfig);
                log.debug("offline api then clean cache");
            } catch (Exception e) {
                log.error("clean cache error", e);
            }
        }

        //如果是集群模式，清除每个apiServer节点内的元数据ehcache缓存
        IMetaDataCacheManager.cleanApiMetaCacheIfCluster(path);
    }

    @Override
    public String getPath(String id) {
        return apiConfigMapper.selectById(id).getPath();
    }

    @Override
    public String apiDocs(List<String> ids) {
        StringBuffer temp = new StringBuffer("# 接口文档\n---\n");
        List<ApiConfig> list = apiConfigMapper.selectBatchIds(ids);
        list.stream().forEach(t -> {
            temp.append("## ").append(t.getName()).append("\n- 接口地址： /api/").append(t.getPath())
                    .append("\n- 接口备注：").append(t.getNote());
            temp.append("\n- Content-Type：").append(t.getContentType());

            temp.append("\n- 请求参数：");
            if (MediaType.APPLICATION_FORM_URLENCODED_VALUE.equalsIgnoreCase(t.getContentType())) {
                String params = t.getParams();
                JSONArray array = JSON.parseArray(params);

                if (array.size() > 0) {
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("\n\n| 参数名称 | 参数类型 | 参数说明 |\n");
                    buffer.append("| :----: | :----: | :----: |\n");

                    for (int i = 0; i < array.size(); i++) {
                        JSONObject jsonObject = array.getJSONObject(i);
                        String name = jsonObject.getString("name");
                        String type = jsonObject.getString("type");
                        if (type.startsWith("Array")) {
                            type = type.substring(6, type.length() - 1) + "数组";
                        }
                        String note = jsonObject.getString("note");
                        buffer.append("|").append(name).append("|").append(type).append("|").append(note).append("|\n");
                    }

                    temp.append(buffer);
                } else {
                    temp.append("无参数\n");
                }
            } else if (MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(t.getContentType())) {
                temp.append("\n```json\n").append(t.getJsonParam()).append("\n```\n");
            }
            temp.append("\n---\n");
        });

        temp.append("\n导出日期：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        return temp.toString();

    }

    @Override
    public JSONObject selectBatch(List<String> ids) {
        List<ApiConfig> list = apiConfigMapper.selectBatchIds(ids);
        List<ApiSql> sqls = apiSqlMapper.selectByApiIds(ids);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("api", list);
        jsonObject.put("sql", sqls);
        return jsonObject;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertBatch(List<ApiConfig> configs, List<ApiSql> sqls) {
        configs.stream().forEach(t -> {
            t.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            t.setUpdateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            t.setStatus(0);
            apiConfigMapper.insert(t);
        });
        sqls.stream().forEach(t -> apiSqlMapper.insert(t));

    }
}
