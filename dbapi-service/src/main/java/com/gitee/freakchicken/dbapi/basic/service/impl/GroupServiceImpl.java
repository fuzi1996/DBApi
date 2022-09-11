package com.gitee.freakchicken.dbapi.basic.service.impl;

import com.gitee.freakchicken.dbapi.basic.domain.Group;
import com.gitee.freakchicken.dbapi.basic.mapper.ApiAuthMapper;
import com.gitee.freakchicken.dbapi.basic.mapper.ApiConfigMapper;
import com.gitee.freakchicken.dbapi.basic.mapper.GroupMapper;
import com.gitee.freakchicken.dbapi.basic.service.IGroupService;
import com.gitee.freakchicken.dbapi.basic.util.UUIDUtil;
import com.gitee.freakchicken.dbapi.dto.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GroupServiceImpl implements IGroupService {
		@Autowired
		private GroupMapper groupMapper;
		@Autowired
		private ApiConfigMapper apiConfigMapper;
		@Autowired
		private ApiAuthMapper apiAuthMapper;

		@Override
		public void insert(Group group) {
				group.setId(UUIDUtil.id());
				groupMapper.insert(group);
		}

		@Override
		@Transactional(rollbackFor = Exception.class)
		public ResponseDTO deleteById(String id) {
				int size = apiConfigMapper.selectCountByGroup(id);
				if (size > 0) {
						return ResponseDTO.fail("group is not empty, can not delete");
				} else {
						groupMapper.deleteById(id);
						apiAuthMapper.deleteByGroupId(id);
						return ResponseDTO.successWithMsg("delete success");
				}

		}

		@Override
		public List<Group> getAll() {
				return groupMapper.selectList(null);
		}

		@Override
		public List<Group> selectBatch(List<String> ids) {
				return groupMapper.selectBatchIds(ids);
		}

		@Override
		@Transactional(rollbackFor = Exception.class)
		public void insertBatch(List<Group> configs) {
				configs.stream().forEach(t -> {
						groupMapper.insert(t);
				});
		}
}
