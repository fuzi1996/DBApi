import request from '/@/utils/request'
import { AxiosResponse } from 'axios'
import { GLOBAL_FRONTENT_URL_PREFIX } from '/@/constant'

const api = {
  listAllDataSource: `${GLOBAL_FRONTENT_URL_PREFIX}/datasource/getAll`,
  updateDataSource: `${GLOBAL_FRONTENT_URL_PREFIX}/datasource/update`,
  addDataSource: `${GLOBAL_FRONTENT_URL_PREFIX}/datasource/add`,
  connectDataSource: `${GLOBAL_FRONTENT_URL_PREFIX}/datasource/connect`,
  loadDataSourceDetail: (id:string) => `${GLOBAL_FRONTENT_URL_PREFIX}/datasource/detail/${id}`,
  deleteDataSource: (id:string) => `${GLOBAL_FRONTENT_URL_PREFIX}/datasource/delete/${id}`,
  exportDataSource: `${GLOBAL_FRONTENT_URL_PREFIX}/datasource/export`,
  importDataSource: `${GLOBAL_FRONTENT_URL_PREFIX}/datasource/import`,
  getAllTables(datasourceId:string) {
    return `${GLOBAL_FRONTENT_URL_PREFIX}/table/getAllTables?sourceId=${datasourceId}`
  },
  listTableColumns: `${GLOBAL_FRONTENT_URL_PREFIX}/table/getAllColumns`
}

export const DATA_SOURCE_IMPORT_URL = api.importDataSource

export interface IColumn {
  fieldTypeName: string
  fieldJavaTypeName: string
  typeName: string
  label: string
}

export interface ITableMetaData {
    label: string
    showColumns: boolean
    columns: IColumn[]
    typeName: string
}

export interface IDataSourceBasicMeta {
  id: string
  name: string
}

export interface IDataSourceMeta extends IDataSourceBasicMeta {
  note: string
  url: string
  username: string
  password: string
  edit_password: boolean
  type: string
  driver: string
  tableSql: string
}

export interface IDataSourceConnectParam {
  url: string
  username: string
  password: string
  edit_password: boolean
  driver: string
}

export async function getAllTables(datasourceId:string):Promise<ITableMetaData[]> {
  const res = await request.post<IResponse<ITableMetaData[]>>(api.getAllTables(datasourceId))
  return res.data.data
}

export async function listAllDataSource():Promise<IDataSourceMeta[]> {
  const res = await request.post<IResponse<IDataSourceMeta[]>>(api.listAllDataSource)
  return res.data.data
}

export async function getTableColumns(sourceId:string, tableName:string):Promise<IColumn[]> {
  const res = await request.post<IResponse<IColumn[]>>(api.listTableColumns,{
    sourceId,
    table: tableName
  })
  return res.data.data
}

export async function loadDataSourceDetail(id:string):Promise<IResponse<IDataSourceMeta>> {
  const res = await request.post<IResponse<IDataSourceMeta>>(api.loadDataSourceDetail(id))
  return res.data
}

export async function deleteDataSourceById(id:string):Promise<IResponse<any>> {
  const res = await request.post<IResponse<any>>(api.deleteDataSource(id))
  return res.data
}

export async function addDataSource(param:IDataSourceMeta):Promise<IResponse<any>> {
  const res = await request.post<IResponse<any>>(api.addDataSource,param)
  return res.data
}

export async function updateDataSource(param:IDataSourceMeta):Promise<IResponse<any>> {
  const res = await request.post<IResponse<any>>(api.updateDataSource,param)
  return res.data
}

export async function connectDataSource(param:IDataSourceConnectParam):Promise<IResponse<any>> {
  const res = await request.post<IResponse<any>>(api.connectDataSource,param)
  return res.data
}

export async function exportDataSource(ids:string):Promise<AxiosResponse<any>> {
  return await request.post<AxiosResponse<any>>(api.exportDataSource,{ ids: ids },{
    responseType: 'blob'
  })
}