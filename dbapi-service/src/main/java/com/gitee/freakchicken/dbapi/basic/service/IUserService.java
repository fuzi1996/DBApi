package com.gitee.freakchicken.dbapi.basic.service;

import com.gitee.freakchicken.dbapi.basic.domain.User;

public interface IUserService {

		User getUser(String username, String password);

		User getUserById(Integer id);

		void resetPassword(String password);
}
