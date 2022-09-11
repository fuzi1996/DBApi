package com.gitee.freakchicken.dbapi.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Objects;

/**
 * @program: dbApi
 * @author: kensan
 * @create: 2022-09-11 21:00
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CloseUtil {
		public static void safeClose(AutoCloseable closeable) {
				if (Objects.nonNull(closeable)) {
						try {
								closeable.close();
						} catch (Exception e) {
								// IGNORE
						}
				}
		}
}
