package com.gitee.freakchicken.dbapi.basic.servlet;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.gitee.freakchicken.dbapi.basic.domain.DataSource;
import com.gitee.freakchicken.dbapi.basic.service.IApiConfigService;
import com.gitee.freakchicken.dbapi.basic.service.IApiService;
import com.gitee.freakchicken.dbapi.basic.service.IDataSourceService;
import com.gitee.freakchicken.dbapi.basic.service.IPService;
import com.gitee.freakchicken.dbapi.basic.util.JdbcUtil;
import com.gitee.freakchicken.dbapi.basic.util.PoolManager;
import com.gitee.freakchicken.dbapi.basic.util.SqlEngineUtil;
import com.gitee.freakchicken.dbapi.basic.util.ThreadUtils;
import com.gitee.freakchicken.dbapi.domain.ApiConfig;
import com.gitee.freakchicken.dbapi.domain.ApiSql;
import com.gitee.freakchicken.dbapi.dto.ResponseDTO;
import com.gitee.freakchicken.dbapi.plugin.AlarmPlugin;
import com.gitee.freakchicken.dbapi.plugin.CachePlugin;
import com.gitee.freakchicken.dbapi.plugin.PluginManager;
import com.gitee.freakchicken.dbapi.plugin.TransformPlugin;
import com.gitee.freakchicken.dbapi.util.CloseUtil;
import com.github.freakchick.orange.SqlMeta;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component
public class APIServlet extends HttpServlet {

    @Autowired
    private IApiConfigService IApiConfigService;
    @Autowired
    private IDataSourceService IDataSourceService;
    @Autowired
    private IApiService IApiService;

    @Autowired
    private IPService ipService;

    @Value("${dbapi.api.context}")
    private String apiContext;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.debug("servlet execute");
        String servletPath = request.getRequestURI();
        servletPath = servletPath.substring(apiContext.length() + 2);

        try {
            ResponseDTO responseDto = process(servletPath, request, response);
            response.getWriter().append(JSON.toJSONString(responseDto));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().append(JSON.toJSONString(ResponseDTO.fail(e.toString())));
            log.error(e.toString(), e);
        } finally {
            if (response.getWriter() != null) {
                response.getWriter().close();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }

    public ResponseDTO process(String path, HttpServletRequest request, HttpServletResponse response) {

        // 校验接口是否存在
        ApiConfig config = IApiConfigService.getConfig(path);
        if (config == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return ResponseDTO.fail("Api not exists");
        }

        try {
            DataSource datasource = IDataSourceService.detail(config.getDatasourceId());
            if (datasource == null) {
                response.setStatus(500);
                return ResponseDTO.fail("Datasource not exists!");
            }

            Map<String, Object> sqlParam = getParams(request, config);

            //从缓存获取数据
            if (StringUtils.isNoneBlank(config.getCachePlugin())) {
                CachePlugin cachePlugin = PluginManager.getCachePlugin(config.getCachePlugin());
                Object o = cachePlugin.get(config, sqlParam);
                if (o != null) {
                    return ResponseDTO.successWithData(o); //如果缓存有数据直接返回
                }
            }

            List<ApiSql> sqlList = config.getSqlList();
            DruidPooledConnection connection = PoolManager.getPooledConnection(datasource);
            //是否开启事务
            boolean flag = config.getOpenTrans() == 1;
            //执行sql
            List<Object> dataList = executeSql(connection, sqlList, sqlParam, flag);

            //执行数据转换
            for (int i = 0; i < sqlList.size(); i++) {
                ApiSql apiSql = sqlList.get(i);
                Object data = dataList.get(i);
                //如果此单条sql是查询类sql，并且配置了数据转换插件
                if (data instanceof Iterable && StringUtils.isNotBlank(apiSql.getTransformPlugin())) {
                    log.info("transform plugin execute");
                    List<JSONObject> sourceData = (List<JSONObject>) (data); //查询类sql的返回结果才可以这样强制转换，只有查询类sql才可以配置转换插件
                    TransformPlugin transformPlugin = PluginManager.getTransformPlugin(apiSql.getTransformPlugin());
                    Object resData = transformPlugin.transform(sourceData, apiSql.getTransformPluginParams());
                    dataList.set(i, resData);//重新设置值
                }
            }
            Object res = dataList;
            //如果只有单条sql,返回结果不是数组格式
            if (dataList.size() == 1) {
                res = dataList.get(0);
            }
            ResponseDTO<Object> dto = ResponseDTO.successWithData(res);
            //设置缓存
            if (StringUtils.isNoneBlank(config.getCachePlugin())) {
                CachePlugin cachePlugin = PluginManager.getCachePlugin(config.getCachePlugin());
                cachePlugin.set(config, sqlParam, dto.getData());
            }
            return dto;
        } catch (Exception e) {
            //如果API配置了告警
            if (StringUtils.isNotBlank(config.getAlarmPlugin())) {
                try {
                    log.info(config.getAlarmPlugin());
                    AlarmPlugin alarmPlugin = PluginManager.getAlarmPlugin(config.getAlarmPlugin());
                    ThreadUtils.submitAlarmTask(new Runnable() {
                        @Override
                        public void run() {
                            alarmPlugin.alarm(e, config, request, config.getAlarmPluginParam());
                        }
                    });
                } catch (Exception error) {
                    log.error(config.getAlarmPlugin() + " error!", error);
                }
            }
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<Object> executeSql(Connection connection, List<ApiSql> sqlList, Map<String, Object> sqlParam, boolean flag) {
        List<Object> dataList = new ArrayList<>();
        try {
            connection.setAutoCommit(!flag);
            for (ApiSql apiSql : sqlList) {
                SqlMeta sqlMeta = SqlEngineUtil.getEngine().parse(apiSql.getSqlText(), sqlParam);
                Object data = JdbcUtil.executeSql(connection, sqlMeta.getSql(), sqlMeta.getJdbcParamValues());
                dataList.add(data);
            }
            if (flag) {
                connection.commit();
            }
            return dataList;
        } catch (Exception e) {
            try {
                if (flag) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                log.warn("事务回滚失败", ex);
            }
            throw new RuntimeException(e);
        } finally {
            CloseUtil.safeClose(connection);
        }
    }

    private Map<String, Object> getParams(HttpServletRequest request, ApiConfig apiConfig) {
        /**
         * Content-Type格式说明:
         * {@see <a href="https://www.w3.org/Protocols/rfc1341/4_Content-Type.html">Content-Type</a>}
         * type/subtype(;parameter)? type
         */
        String unParseContentType = request.getContentType();

        //如果是浏览器get请求过来，取出来的contentType是null
        if (unParseContentType == null) {
            unParseContentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE;
        }
        // issues/I57ZG2
        // 解析contentType 格式: appliation/json;charset=utf-8
        String[] contentTypeArr = unParseContentType.split(";");
        String contentType = contentTypeArr[0];

        Map<String, Object> params = null;
        //如果是application/json请求，不管接口规定的content-type是什么，接口都可以访问，且请求参数都以json body 为准
        if (contentType.equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE)) {
            JSONObject jo = getHttpJsonBody(request);
            if (Objects.nonNull(jo)) {
                params = JSONObject.parseObject(jo.toJSONString(), new TypeReference<Map<String, Object>>() {
                });
            } else {
                params = new LinkedHashMap<>();
            }
        }
        //如果是application/x-www-form-urlencoded请求，先判断接口规定的content-type是不是确实是application/x-www-form-urlencoded
        else if (contentType.equalsIgnoreCase(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
            if (MediaType.APPLICATION_FORM_URLENCODED_VALUE.equalsIgnoreCase(apiConfig.getContentType())) {
                params = IApiService.getSqlParam(request, apiConfig);
            } else {
                throw new RuntimeException("this API only support content-type: " + apiConfig.getContentType() + ", but you use: " + contentType);
            }
        } else {
            throw new RuntimeException("content-type not supported: " + contentType);
        }

        return params;
    }

    private JSONObject getHttpJsonBody(HttpServletRequest request) {
        try (InputStreamReader in = new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(in)) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return JSON.parseObject(sb.toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

}
