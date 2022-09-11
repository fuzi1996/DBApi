package com.gitee.freakchicken.dbapi.basic.controller;

import com.gitee.freakchicken.dbapi.basic.domain.Group;
import com.gitee.freakchicken.dbapi.basic.service.IGroupService;
import com.gitee.freakchicken.dbapi.dto.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/group")
public class GroupController {

    @Autowired
    private IGroupService IGroupService;

    @RequestMapping("/create")
    public void create(Group group) {
        IGroupService.insert(group);
    }

    @RequestMapping("/delete/{id}")
    public ResponseDTO delete(@PathVariable String id) {
        return IGroupService.deleteById(id);
    }

    @RequestMapping("/getAll")
    public List<Group> getAll() {
        return IGroupService.getAll();
    }
}
