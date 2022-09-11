package com.gitee.freakchicken.dbapi.basic.controller;

import com.gitee.freakchicken.dbapi.basic.domain.AppInfo;
import com.gitee.freakchicken.dbapi.basic.service.IAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/app")
public class AppController {

    @Autowired
    private IAppService IAppService;

    /**
     * 创建应用
     * @param app
     * @return
     */
    @PostMapping("/create")
    public AppInfo createApp(AppInfo app) {
        return IAppService.add(app);
    }

    @PostMapping("/getAll")
    public List<AppInfo> getAll() {
        return IAppService.getAll();
    }

    @PostMapping("/delete/{appid}")
    public void delete(@PathVariable("appid") String appid) {
        IAppService.delete(appid);
    }

    @PostMapping("/auth")
    public void auth(String appId, String groupIds) {
        IAppService.auth(appId, groupIds);
    }

    @PostMapping("/getAuthGroups")
    public List<String> getAuthGroups(String appId) {
        return IAppService.getAuthGroups(appId);
    }
}