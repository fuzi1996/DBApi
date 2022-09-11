package com.gitee.freakchicken.dbapi.basic.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @program: dbApi
 * @author: kensan
 * @create: 2022-09-11 17:18
 */
@Slf4j
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseUtil {
		public static void writeUTF8Data(HttpServletResponse response, String content) {
				try (OutputStream os = response.getOutputStream()) {
						os.write(content.getBytes(StandardCharsets.UTF_8));
				} catch (Exception e) {
						log.warn("write data error", e);
				}
		}
}
