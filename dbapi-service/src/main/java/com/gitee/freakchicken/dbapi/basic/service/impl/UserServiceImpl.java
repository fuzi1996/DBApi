package com.gitee.freakchicken.dbapi.basic.service.impl;

import com.gitee.freakchicken.dbapi.basic.domain.User;
import com.gitee.freakchicken.dbapi.basic.mapper.UserMapper;
import com.gitee.freakchicken.dbapi.basic.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements IUserService {

		@Autowired
		private UserMapper userMapper;

		@Override
		public User getUser(String username, String password) {
				return userMapper.login(username, password);
		}

		@Override
		public User getUserById(Integer id) {
				return userMapper.selectById(id);
		}

		@Override
		@Transactional(rollbackFor = Exception.class)
		public void resetPassword(String password) {
				userMapper.updatePassword(password);
		}
}
