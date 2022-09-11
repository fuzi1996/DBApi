package com.gitee.freakchicken.dbapi.basic.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gitee.freakchicken.dbapi.basic.domain.DataSource;
import com.gitee.freakchicken.dbapi.util.CloseUtil;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JdbcUtil {

    public static ResultSet query(String sql, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        return preparedStatement.executeQuery();
    }

    public static Connection getConnection(DataSource ds) throws Exception {
        try {
            Class.forName(ds.getDriver());
            String password = ds.isEdit_password() ? ds.getPassword() : DESUtils.decrypt(ds.getPassword());
            Connection connection = DriverManager.getConnection(ds.getUrl(), ds.getUsername(),password);
            log.info("successfully connected");
            return connection;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Please check whether the jdbc driver jar is missing, if missed copy the jdbc jar file to lib dir. " + e.getMessage());
        }
    }

    /**
     * 查询库中所有表
     *
     * @param conn
     * @param sql
     * @return
     */
    public static List<String> getAllTables(Connection conn, String sql) {
        List<String> list = new ArrayList<>();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            ResultSet resultSet = pst.executeQuery();
            while (resultSet.next()) {
                String s = resultSet.getString(1);
                list.add(s);
            }
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            CloseUtil.safeClose(conn);
        }
    }

    /**
     * 查询表所有字段
     *
     * @param conn
     * @param type
     * @param table
     * @return
     */
    public static List<JSONObject> getRDBMSColumnProperties(Connection conn, String type, String table) {
        List<JSONObject> list = new ArrayList<>();
        String sql;
        switch (type) {
            case "POSTGRESQL":
                sql = "select * from \"" + table + "\" where 1=2";
                break;
            default:
                sql = "select * from " + table + " where 1=2";
        }
        try (PreparedStatement pst = conn.prepareStatement(sql);) {
            ResultSetMetaData rsd = pst.executeQuery().getMetaData();

            for (int i = 0; i < rsd.getColumnCount(); i++) {
                JSONObject jsonObject = new JSONObject();

                String columnTypeName = rsd.getColumnTypeName(i + 1);
                jsonObject.put("fieldTypeName", columnTypeName);//数据库字段类型名
                jsonObject.put("typeName", columnTypeName);
                jsonObject.put("fieldJavaTypeName", rsd.getColumnClassName(i + 1));//映射到java的类型名
                String columnName = rsd.getColumnName(i + 1);
                if (columnName.contains(".")) {
                    columnName = columnName.split("\\.")[1];
                }
                jsonObject.put("label", columnName);//表字段
                list.add(jsonObject);
            }
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            CloseUtil.safeClose(conn);
        }
    }

    /**
     * 没有关闭连接，需要在调用方关闭
     *
     * @param connection
     * @param sql
     * @param jdbcParamValues
     * @return
     */
    public static Object executeSql(Connection connection, String sql, List<Object> jdbcParamValues) throws SQLException {
        log.debug(sql);
        log.debug(JSON.toJSONString(jdbcParamValues));
        PreparedStatement statement = connection.prepareStatement(sql);
        //参数注入
        for (int i = 1; i <= jdbcParamValues.size(); i++) {
            statement.setObject(i, jdbcParamValues.get(i - 1));
        }
        boolean hasResultSet = statement.execute();

        if (hasResultSet) {
            ResultSet rs = statement.getResultSet();
            int columnCount = rs.getMetaData().getColumnCount();

            List<String> columns = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = rs.getMetaData().getColumnLabel(i);
                columns.add(columnName);
            }
            List<JSONObject> list = new ArrayList<>();
            while (rs.next()) {
                JSONObject jo = new JSONObject();
                columns.forEach(t -> {
                    try {
                        Object value = rs.getObject(t);
                        jo.put(t, value);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                });
                list.add(jo);
            }
            return list;
        } else {
            int updateCount = statement.getUpdateCount();
            return updateCount + " rows affected";
        }

    }
}