package com.gitee.freakchicken.dbapi.basic.service;

import com.gitee.freakchicken.dbapi.basic.domain.DataSource;
import com.gitee.freakchicken.dbapi.dto.ResponseDTO;

import java.util.List;

/**
 * @program: dbApi
 * @description:
 * @author:
 * @create: 2021-01-20 10:43
 **/
public interface IDataSourceService {

    void add(DataSource dataSource);

    void update(DataSource dataSource);

    ResponseDTO delete(String id);

    DataSource detail(String id);

    List<DataSource> getAll();

    String getDBType(Integer id);

    List<DataSource> selectBatch(List<String> ids);

    void insertBatch(List<DataSource> list);
}
