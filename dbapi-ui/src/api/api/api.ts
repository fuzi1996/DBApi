import request from '/@/utils/request'
import { AxiosResponse } from 'axios'
import { GLOBAL_FRONTENT_URL_PREFIX } from '/@/constant'

const api = {
  listAllApi: `${GLOBAL_FRONTENT_URL_PREFIX}/apiConfig/getAll`,
  getContext: `${GLOBAL_FRONTENT_URL_PREFIX}/apiConfig/context`,
  getIPAndPort: `${GLOBAL_FRONTENT_URL_PREFIX}/apiConfig/getIPPort`,
  getIP: `${GLOBAL_FRONTENT_URL_PREFIX}/apiConfig/getIP`,
  getMode: `${GLOBAL_FRONTENT_URL_PREFIX}/apiConfig/mode`,
  parseParam: `${GLOBAL_FRONTENT_URL_PREFIX}/apiConfig/parseParam`,
  sqlExecute: `${GLOBAL_FRONTENT_URL_PREFIX}/apiConfig/sql/execute`,
  parseDynamicSql: `${GLOBAL_FRONTENT_URL_PREFIX}/apiConfig/parseDynamicSql`,
  addApi: `${GLOBAL_FRONTENT_URL_PREFIX}/apiConfig/add`,
  updateApi: `${GLOBAL_FRONTENT_URL_PREFIX}/apiConfig/update`,
  loadApi: (id:string) => {
    return `${GLOBAL_FRONTENT_URL_PREFIX}/apiConfig/detail/${id}`
  },
  deleteApi: (id:string) => {
    return `${GLOBAL_FRONTENT_URL_PREFIX}/apiConfig/delete/${id}`
  },
  offlineApi: (id:string) => {
    return `${GLOBAL_FRONTENT_URL_PREFIX}/apiConfig/offline/${id}`
  },
  onlineApi: (id:string) => {
    return `${GLOBAL_FRONTENT_URL_PREFIX}/apiConfig/online/${id}`
  }
}

export interface IApiSqlItem {
    id: string,
    apiId: string,
    label: string,
    sqlText: string,
    transformPlugin: string,
    transformPluginParams: string,
}

export interface IApiBasicConfig{
    id: string,
    name: string,
    note: string,
    path: string,
     
    datasourceId: string,
    sqlList: IApiSqlItem[],
    params: string,
    previlege: number,
    groupId: string,
    
    contentType: string,
    jsonParam: string,

    createTime: string,
    updateTime: string,
    status: number,
}

export interface IApiAdvanceConfig {
  cachePlugin: string,
  cachePluginParams: string,
  openTrans: number,
  alarmPlugin: string,
  alarmPluginParam: string
}

export interface IApiConfigItem extends IApiBasicConfig,IApiAdvanceConfig {

}

export interface IFormParamItem {
  name: string,
  type: string,
  note: string
}

export interface IApiBasicConfigEditItem extends IApiBasicConfig {
  formParam: IFormParamItem[]
}

export interface IApiConfigSaveParam extends IApiConfigItem {
  formParam: IFormParamItem[]
}

export interface IParamItem {
  value: string
}

export interface IParseDynamicSqlResult {
  sql: string
  jdbcParamValues: any[]
}

export function getAllApis():Promise<AxiosResponse<IResponse<IApiConfigItem[]>>> {
  return request({
    url: api.listAllApi,
    method: 'get'
  })
}

export async function getContext():Promise<string> {
  const res = await request.post<IResponse<string>>(api.getContext)
  return res.data.data
}

export async function getIPAndPort():Promise<string> {
  const res = await request.post<IResponse<string>>(api.getIPAndPort)
  return res.data.data
}

export async function getIP():Promise<string> {
  const res = await request.post<IResponse<string>>(api.getIP)
  return res.data.data
}

export async function getMode():Promise<string> {
  const res = await request.post<IResponse<string>>(api.getMode)
  return res.data.data
}

export async function parseParam():Promise<IParamItem[]> {
  const res = await request.post<IResponse<IParamItem[]>>(api.parseParam)
  return res.data.data
}

export async function executeSql(sql:string,datasourceId:string,params:any):Promise<IResponse<any[]>> {
  const res = await request.post<IResponse<any[]>>(api.sqlExecute,{
    sql,
    datasourceId,
    params
  })
  return res.data
}

export async function parseDynamicSql(sql:string,params:any):Promise<IResponse<IParseDynamicSqlResult>> {
  const res = await request.post<IResponse<IParseDynamicSqlResult>>(api.parseDynamicSql,{
    sql,
    params
  })
  return res.data
}

export async function addApi(data:IApiConfigItem):Promise<IResponse<any>> {
  const res = await request.post<IResponse<IParseDynamicSqlResult>>(api.addApi,data,{ headers: { 'Content-Type': 'application/json' } })
  return res.data
}

export async function updateApi(data:IApiConfigItem):Promise<IResponse<any>> {
  const res = await request.post<IResponse<IParseDynamicSqlResult>>(api.updateApi,data,{ headers: { 'Content-Type': 'application/json' } })
  return res.data
}

export async function loadApi(id:string):Promise<IApiConfigItem> {
  const res = await request.post<IResponse<IApiConfigItem>>(api.loadApi(id))
  return res.data.data
}

export async function deleteApi(id:string):Promise<IResponse<any>> {
  const res = await request.post<IResponse<any>>(api.deleteApi(id))
  return res.data
}

export async function onlineApi(id:string):Promise<IResponse<any>> {
  const res = await request.post<IResponse<any>>(api.onlineApi(id))
  return res.data
}

export async function offlineApi(id:string):Promise<IResponse<any>> {
  const res = await request.post<IResponse<any>>(api.offlineApi(id))
  return res.data
}