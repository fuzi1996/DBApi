package com.gitee.freakchicken.dbapi.basic.controller;

import com.gitee.freakchicken.dbapi.basic.domain.User;
import com.gitee.freakchicken.dbapi.basic.service.IUserService;
import com.gitee.freakchicken.dbapi.basic.util.JwtUtils;
import com.gitee.freakchicken.dbapi.dto.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

		@Autowired
		private IUserService IUserService;

		@RequestMapping("/login")
		public ResponseDTO<Object> login(String username, String password) {
				User user = IUserService.getUser(username, password);
				if (user == null) {
						return ResponseDTO.fail("username or password error");
				} else {
						String token = JwtUtils.createToken(user.getId().toString(), user.getPassword());
						return ResponseDTO.successWithData(token);
				}
		}

		@RequestMapping("/resetPassword")
		public void resetPassword(String password) {
				IUserService.resetPassword(password);
		}

}
