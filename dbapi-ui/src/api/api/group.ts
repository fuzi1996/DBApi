import request from '/@/utils/request'
import { AxiosResponse } from 'axios'
import { GLOBAL_FRONTENT_URL_PREFIX } from '/@/constant'

const api = {
  listAllGroup: `${GLOBAL_FRONTENT_URL_PREFIX}/group/getAll`,
  createApiGroup: `${GLOBAL_FRONTENT_URL_PREFIX}/group/create`,
  deleteApiGroup: (id:string):string => {
    return `${GLOBAL_FRONTENT_URL_PREFIX}/group/delete/${id}`
  } 
}

export interface IGroupItem {
    id: string,
    name: string
}

export function listAllGroup():Promise<AxiosResponse<IResponse<IGroupItem[]>>> {
  return request({
    url: api.listAllGroup,
    method: 'get'
  })
}

export function createGroup(name:string):Promise<AxiosResponse<IResponse<unknown>>> {
  return request({
    url: api.createApiGroup,
    method: 'post',
    params:{
      name
    }
  })
}

export function deleteGroup(id:string):Promise<AxiosResponse<IResponse<unknown>>> {
  return request({
    url: api.deleteApiGroup(id),
    method: 'post'
  })
}