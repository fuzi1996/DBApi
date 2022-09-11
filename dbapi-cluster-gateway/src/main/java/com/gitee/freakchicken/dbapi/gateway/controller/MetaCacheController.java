package com.gitee.freakchicken.dbapi.gateway.controller;

import com.gitee.freakchicken.dbapi.basic.service.IPService;
import com.gitee.freakchicken.dbapi.dto.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/metacache")
public class MetaCacheController {

    @Autowired
    private IPService ipService;

    @RequestMapping("/iprule/sync")
    public ResponseDTO hello(){
        ipService.init();
        return ResponseDTO.successWithData(null);
    }
}
