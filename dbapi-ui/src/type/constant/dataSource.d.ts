export interface IDataSourceMetaItem {
  url: string,
  driver: string,
  sql: string
}
  
export interface IDataSourceMetaObject {
  [index: string]: IDataSourceMetaItem
}