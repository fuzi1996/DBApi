package com.gitee.freakchicken.dbapi.basic.service;

import com.gitee.freakchicken.dbapi.basic.domain.Group;
import com.gitee.freakchicken.dbapi.dto.ResponseDTO;

import java.util.List;


public interface IGroupService {

		void insert(Group group);

		ResponseDTO deleteById(String id);

		List<Group> getAll();

		List<Group> selectBatch(List<String> ids);

		void insertBatch(List<Group> configs);
}
