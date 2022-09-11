package com.gitee.freakchicken.dbapi.basic.controller;

import com.gitee.freakchicken.dbapi.basic.service.IPService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/firewall")
public class FirewallController {

    @Autowired
    private IPService ipService;

    @RequestMapping("/save")
    public void save(String status, String mode, String ips) {
        if (status.equals("on")) {
            if (mode.equals("white")) {
                ipService.on(mode, ips);

            } else if (mode.equals("black")) {
                ipService.on(mode, ips);
            }
        } else if (status.equals("off")) {
            ipService.off();
        }

    }

    @RequestMapping("/detail")
    public Map<String, String> detail() {
        return ipService.detail();
    }
}
