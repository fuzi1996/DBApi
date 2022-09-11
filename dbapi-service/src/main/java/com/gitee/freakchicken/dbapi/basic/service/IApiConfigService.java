package com.gitee.freakchicken.dbapi.basic.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gitee.freakchicken.dbapi.domain.ApiConfig;
import com.gitee.freakchicken.dbapi.domain.ApiSql;
import com.gitee.freakchicken.dbapi.dto.ResponseDTO;

import java.util.List;

/**
 * @program: dbApi
 * @description:
 * @author: jiangqiang
 * @create: 2021-01-19 17:27
 **/
public interface IApiConfigService {
    
    ResponseDTO add(ApiConfig apiConfig);

    ResponseDTO update(ApiConfig apiConfig);

    void delete(String id);

    ApiConfig detail(String id);

    List<ApiConfig> getAll();

    JSONArray getAllDetail();

    List<ApiConfig> search(String keyword, String field, String groupId);

    /**
     * servlet 从这获取API元数据
     */
    ApiConfig getConfig(String path);

    void online(String id, String path);

    void offline(String id, String path);

    String getPath(String id);

    String apiDocs(List<String> ids);

    JSONObject selectBatch(List<String> ids);

    void insertBatch(List<ApiConfig> configs, List<ApiSql> sqls);
}
