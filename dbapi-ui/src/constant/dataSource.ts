import { IDataSourceMetaObject } from '/@/type/constant/dataSource'
import { OptionItem } from '/@/type/constant/common'

const DATASOURCE_META_DATA:IDataSourceMetaObject = {
  mysql: {
    url: 'jdbc:mysql://localhost:3306/<db>?useSSL=false&characterEncoding=UTF-8&serverTimezone=GMT%2B8',
    driver: 'com.mysql.cj.jdbc.Driver',
    sql: 'show tables'
  },
  postgresql: {
    url: 'jdbc:postgresql://localhost:5432/<db>',
    driver: 'org.postgresql.Driver',
    sql: 'SELECT table_name FROM information_schema.tables WHERE table_schema = \'public\' ORDER BY table_name'
  },
  hive: {
    url: 'jdbc:hive2://localhost:10000/<db>',
    driver: 'org.apache.hive.jdbc.HiveDriver',
    sql: 'show tables'
  },
  sqlserver: {
    url: 'jdbc:microsoft:sqlserver://localhost:1433;databaseName=<db>',
    driver: 'com.microsoft.sqlserver.jdbc.SQLServerDriver',
    sql: 'select * from sys.tables'
  },
  clickhouse: {
    url: 'jdbc:clickhouse://localhost:8123/<db>',
    driver: 'ru.yandex.clickhouse.ClickHouseDriver',
    sql:''
  },
  kylin: {
    url: 'jdbc:kylin://localhost:7070/<db>',
    driver: 'org.apache.kylin.jdbc.Driver',
    sql:''
  },
  oracle: {
    url: 'jdbc:oracle:thin:@localhost:1521:<db>',
    driver: 'oracle.jdbc.OracleDriver',
    sql: 'SELECT OWNER || \'.\' || TABLE_NAME FROM ALL_TABLES'
  },
  TDengine:{
    url: 'jdbc:TAOS://localhost:6030/<db>?timezone=Asia/Beijing&charset=UTF-8',
    driver: 'com.taosdata.jdbc.rs.RestfulDriver',
    sql: ''
  },
  doris:{
    url: 'jdbc:mysql://localhost:9030/<db>',
    driver: 'com.mysql.cj.jdbc.Driver',
    sql: ''
  }
}

const DB_OPTIONS:Array<OptionItem<string>> = [
  { label: 'mysql', value: 'mysql' }, 
  { label: 'postgresql',value: 'postgresql' }, 
  { label: 'hive',value: 'hive' },
  { label: 'sqlserver',value: 'sqlserver' }, 
  { label: 'clickhouse',value: 'clickhouse' }, 
  { label: 'kylin',value: 'kylin' },
  { label: 'oracle',value: 'oracle' }, 
  { label: 'TDengine',value: 'TDengine' },
  { label: 'doris',value: 'doris' },
  { label: '其他',value:'others' }]

const DB_TRANSACTION_MODE:Array<OptionItem<number>> = [
  { label: '开启', value: 1 }, 
  { label: '关闭',value: 0 }
]

export {
  DATASOURCE_META_DATA,
  DB_OPTIONS,
  DB_TRANSACTION_MODE
}