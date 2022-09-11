package com.gitee.freakchicken.dbapi.basic.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AppToken {
		private String appId;
		private String token;
		private Long expireTime;
}