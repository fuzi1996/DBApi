import request from '/@/utils/request'
import { AxiosResponse } from 'axios'
import { GLOBAL_FRONTENT_URL_PREFIX } from '/@/constant'

const api = {
  create: `${GLOBAL_FRONTENT_URL_PREFIX}/app/create`,
  list: `${GLOBAL_FRONTENT_URL_PREFIX}/app/getAll`,
  auth: `${GLOBAL_FRONTENT_URL_PREFIX}/app/auth`,
  delete: (id:string):string => `${GLOBAL_FRONTENT_URL_PREFIX}/app/delete/${id}`,
  authGroups:`${GLOBAL_FRONTENT_URL_PREFIX}/app/getAuthGroups`
}

export interface ICreateAppParam {
    name: string,
    note: string,
    expireDesc: string
}

export interface IAppItem {
    id: string,
    secret: string,
    name: string,
    note: string,
    expireDesc: string,
    expireTime: number
}

export interface IAppAuthParam{
    appId: string,
    groupIds: string[]
}

export function createApp(param:ICreateAppParam):Promise<AxiosResponse<IResponse<IAppItem>>> {
  return request({
    url: api.create,
    method: 'post',
    params: param
  })
}

export function getAllApp():Promise<AxiosResponse<IResponse<IAppItem[]>>> {
  return request({
    url: api.list,
    method: 'post'
  })
}

export function authApp(param: IAppAuthParam):Promise<AxiosResponse<IResponse<unknown>>> {
  const { groupIds } = param
  let newGroupIds = ''
  if(Array.isArray(groupIds)) {
    newGroupIds = groupIds.join(',')
  }
  return request({
    url: api.auth,
    method: 'post',
    params: {
      appId: param.appId,
      groupIds: newGroupIds
    }
  })
}
export function deleteApp(id:string):Promise<AxiosResponse<IResponse<unknown>>> {
  return request({
    url: api.delete(id),
    method: 'post'
  })
}
export function loadAuthGroups(appId:string):Promise<AxiosResponse<IResponse<string[]>>> {
  return request({
    url: api.authGroups,
    method: 'post',
    params: {
      appId
    }
  })
}