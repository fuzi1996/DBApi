package com.gitee.freakchicken.dbapi.basic.controller;

import com.alibaba.fastjson.JSON;
import com.gitee.freakchicken.dbapi.basic.domain.DataSource;
import com.gitee.freakchicken.dbapi.basic.service.IDataSourceService;
import com.gitee.freakchicken.dbapi.basic.util.JdbcUtil;
import com.gitee.freakchicken.dbapi.basic.util.ResponseUtil;
import com.gitee.freakchicken.dbapi.dto.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

/**
 * @program: dbApi
 * @description:
 * @author: jiangqiang
 * @create: 2021-01-20 10:43
 **/
@Slf4j
@RestController
@RequestMapping("/datasource")
public class DataSourceController {

		@Autowired
		private IDataSourceService IDataSourceService;

		@RequestMapping("/add")
		public void add(DataSource dataSource) {
				IDataSourceService.add(dataSource);
		}

		@RequestMapping("/getAll")
		public List<DataSource> getAll() {
				return IDataSourceService.getAll();
		}

		@RequestMapping("/detail/{id}")
		public DataSource detail(@PathVariable String id) {
				return IDataSourceService.detail(id);
		}

		@RequestMapping("/delete/{id}")
		public ResponseDTO delete(@PathVariable String id) {
				return IDataSourceService.delete(id);
		}

		@RequestMapping("/update")
		public void update(DataSource dataSource) {
				IDataSourceService.update(dataSource);
		}

		@RequestMapping("/connect")
		public ResponseDTO connect(DataSource dataSource) {
				try (Connection connection = JdbcUtil.getConnection(dataSource)) {
						return ResponseDTO.SUCCESS;
				} catch (Exception e) {
						log.error(e.getMessage(), e);
						return ResponseDTO.fail(e.getMessage());
				}
		}

		@RequestMapping("/export")
		public void export(String ids, HttpServletResponse response) {
				List<String> collect = Arrays.asList(ids.split(","));
				List<DataSource> list = IDataSourceService.selectBatch(collect);
				String content = JSON.toJSONString(list);
				response.setContentType("application/x-msdownload;charset=utf-8");
				response.setHeader("Content-Disposition", "attachment; filename=datasource.json");
				ResponseUtil.writeUTF8Data(response, content);
		}


		@RequestMapping(value = "/import", produces = "application/json;charset=UTF-8")
		public void uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
				String s = IOUtils.toString(file.getInputStream(), StandardCharsets.UTF_8);
				List<DataSource> list = JSON.parseArray(s, DataSource.class);
				IDataSourceService.insertBatch(list);
		}
}
