package com.gitee.freakchicken.dbapi.basic.controller;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gitee.freakchicken.dbapi.basic.domain.DataSource;
import com.gitee.freakchicken.dbapi.basic.domain.Group;
import com.gitee.freakchicken.dbapi.basic.service.IApiConfigService;
import com.gitee.freakchicken.dbapi.basic.service.IDataSourceService;
import com.gitee.freakchicken.dbapi.basic.service.IGroupService;
import com.gitee.freakchicken.dbapi.basic.service.INacosService;
import com.gitee.freakchicken.dbapi.basic.util.JdbcUtil;
import com.gitee.freakchicken.dbapi.basic.util.PoolManager;
import com.gitee.freakchicken.dbapi.basic.util.ResponseUtil;
import com.gitee.freakchicken.dbapi.basic.util.SqlEngineUtil;
import com.gitee.freakchicken.dbapi.constant.ModeConstant;
import com.gitee.freakchicken.dbapi.domain.ApiConfig;
import com.gitee.freakchicken.dbapi.domain.ApiSql;
import com.gitee.freakchicken.dbapi.dto.ResponseDTO;
import com.gitee.freakchicken.dbapi.util.CloseUtil;
import com.github.freakchick.orange.SqlMeta;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @program: dbApi
 * @description:
 * @author: jiangqiang
 * @create: 2021-01-19 17:27
 **/
@RestController
@Slf4j
@RequestMapping("/apiConfig")
public class ApiConfigController {

    @Value("${dbapi.mode}")
    private String mode;

    @Autowired
    private IApiConfigService IApiConfigService;

    @Autowired
    private IDataSourceService IDataSourceService;

    @Autowired
    private IGroupService IGroupService;

    @Autowired
    private INacosService INacosService;

    @Value("${dbapi.api.context}")
    private String apiContext;

    @RequestMapping("/context")
    public String getContext() {
        return apiContext;
    }

    @RequestMapping("/add")
    public ResponseDTO add(@RequestBody ApiConfig apiConfig) {
        return IApiConfigService.add(apiConfig);
    }

    @RequestMapping("/parseParam")
    public ResponseDTO parseParam(String sql) {
        try {
            Set<String> set = SqlEngineUtil.getEngine().parseParameter(sql);
            // 转化成前端需要的格式
            List<JSONObject> list = set.stream().map(t -> {
                JSONObject object = new JSONObject();
                object.put("value", t);
                return object;
            }).collect(Collectors.toList());
            return ResponseDTO.successWithData(list);
        } catch (Exception e) {
            return ResponseDTO.fail(e.getMessage());
        }
    }

    @RequestMapping("/getAll")
    public List<ApiConfig> getAll() {
        return IApiConfigService.getAll();
    }

    //给前端使用的数据结构
    @RequestMapping("/getApiTree")
    public JSONArray getApiTree() {
        return IApiConfigService.getAllDetail();
    }

    @RequestMapping("/search")
    public List<ApiConfig> search(String keyword, String field, String groupId) {
        return IApiConfigService.search(keyword, field, groupId);
    }

    @RequestMapping("/detail/{id}")
    public ApiConfig detail(@PathVariable String id) {
        return IApiConfigService.detail(id);
    }

    @RequestMapping("/delete/{id}")
    public void delete(@PathVariable String id) {
        IApiConfigService.delete(id);
    }

    @RequestMapping("/update")
    public ResponseDTO<Object> update(@RequestBody ApiConfig apiConfig) {
        return IApiConfigService.update(apiConfig);
    }

    @RequestMapping("/online/{id}")
    public void online(@PathVariable String id) {
        String path = IApiConfigService.getPath(id);
        IApiConfigService.online(id, path);
    }

    @RequestMapping("/offline/{id}")
    public void offline(@PathVariable String id) {
        String path = IApiConfigService.getPath(id);
        IApiConfigService.offline(id, path);
    }

    @RequestMapping("/getIPPort")
    public String getIPPort(HttpServletRequest request) {
        if (ModeConstant.STANDALONE.equals(mode)) {
            return request.getServerName() + ":" + request.getServerPort() + "/" + apiContext;
        } else if (ModeConstant.CLUSTER.equals(mode)) {
            return INacosService.getGatewayAddress() + "/" + apiContext;
        } else {
            return "";
        }
    }

    @RequestMapping("/getIP")
    public String getIP(HttpServletRequest request) {
        if (ModeConstant.STANDALONE.equals(mode)) {
            return request.getServerName() + ":" + request.getServerPort();
        } else if (ModeConstant.CLUSTER.equals(mode)) {
            return INacosService.getGatewayAddress();
        } else {
            return null;
        }
    }

    @RequestMapping("/mode")
    public String mode() {
        String docker = System.getenv("DOCKER");
        if ("true".equals(docker)) {
            return mode + " in docker";
        } else {
            return mode;
        }
    }

    @RequestMapping("/apiDocs")
    public void apiDocs(String ids, HttpServletResponse response) {
        List<String> collect = Arrays.asList(ids.split(","));
        String docs = IApiConfigService.apiDocs(collect);
        response.setContentType("application/x-msdownload;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=API docs.md");
        ResponseUtil.writeUTF8Data(response, docs);
    }

    @RequestMapping("/downloadConfig")
    public void downloadConfig(String ids, HttpServletResponse response) {
        List<String> collect = Arrays.asList(ids.split(","));
        JSONObject jo = IApiConfigService.selectBatch(collect);
        String content = jo.toString(SerializerFeature.WriteMapNullValue);
        response.setContentType("application/x-msdownload;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=api_config.json");
        ResponseUtil.writeUTF8Data(response, content);
    }

    @RequestMapping("/downloadGroupConfig")
    public void downloadGroupConfig(String ids, HttpServletResponse response) {
        List<String> collect = Arrays.asList(ids.split(","));
        List<Group> list = IGroupService.selectBatch(collect);
        String content = JSON.toJSONString(list);
        response.setContentType("application/x-msdownload;charset=utf-8");
        ResponseUtil.writeUTF8Data(response, content);
    }

    @RequestMapping(value = "/import", produces = "application/json;charset=UTF-8")
    public void uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String s = IOUtils.toString(file.getInputStream(), "utf-8");
        JSONObject jsonObject = JSON.parseObject(s);
        List<ApiConfig> configs = JSON.parseArray(jsonObject.getJSONArray("api").toJSONString(), ApiConfig.class);
        List<ApiSql> sqls = JSON.parseArray(jsonObject.getJSONArray("sql").toJSONString(), ApiSql.class);
        IApiConfigService.insertBatch(configs, sqls);
    }

    @RequestMapping(value = "/importGroup", produces = "application/json;charset=UTF-8")
    public void importGroup(@RequestParam("file") MultipartFile file) throws IOException {
        String s = IOUtils.toString(file.getInputStream(), "utf-8");
        List<Group> configs = JSON.parseArray(s, Group.class);
        IGroupService.insertBatch(configs);
    }

    @RequestMapping("/sql/execute")
    public ResponseDTO<Object> executeSql(String datasourceId, String sql, String params) {
        DruidPooledConnection connection = null;
        try {
            DataSource dataSource = IDataSourceService.detail(datasourceId);
            connection = PoolManager.getPooledConnection(dataSource);
            Map<String, Object> map = JSON.parseObject(params, Map.class);
            SqlMeta sqlMeta = SqlEngineUtil.getEngine().parse(sql, map);
            Object data = JdbcUtil.executeSql(connection, sqlMeta.getSql(), sqlMeta.getJdbcParamValues());
            return ResponseDTO.successWithData(data);
        } catch (Exception e) {
            return ResponseDTO.fail(e.getMessage());
        } finally {
            CloseUtil.safeClose(connection);
        }
    }

    @RequestMapping("/parseDynamicSql")
    public ResponseDTO<Object> parseDynamicSql(String sql, String params) {
        try {
            Map<String, Object> map = JSON.parseObject(params, Map.class);
            SqlMeta sqlMeta = SqlEngineUtil.getEngine().parse(sql, map);
            return ResponseDTO.successWithData(sqlMeta);
        } catch (Exception e) {
            return ResponseDTO.fail(e.getMessage());
        }
    }

}
