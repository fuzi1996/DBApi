package com.gitee.freakchicken.dbapi.basic.service;

import com.gitee.freakchicken.dbapi.domain.ApiConfig;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @program: dbApi
 * @author: jiangqiang
 * @create: 2021-01-20 15:36
 **/
public interface IApiService {

    Map<String, Object> getSqlParam(HttpServletRequest request, ApiConfig config);

}
