package com.gitee.freakchicken.dbapi.basic.service;

import com.gitee.freakchicken.dbapi.basic.domain.AppInfo;

import java.util.List;

public interface IAppService {
    AppInfo add(AppInfo app);

    List<AppInfo> getAll();

    void delete(String appid);

    void auth(String appId, String groupIds);

    List<String> getAuthGroups(String appId);
}