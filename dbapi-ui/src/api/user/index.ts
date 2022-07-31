import request from '/@/utils/request'
import { AxiosResponse } from 'axios'
import { GLOBAL_FRONTENT_URL_PREFIX } from '/@/constant'

const api = {
  login: `${GLOBAL_FRONTENT_URL_PREFIX}/user/login`,
  changePassword: `${GLOBAL_FRONTENT_URL_PREFIX}/user/resetPassword`
}

export interface loginParam {
    username: string,
    password: string
}

export interface changePasswordParam {
    password: string
}

export function login(param: loginParam):Promise<AxiosResponse<IResponse<string>>> {
  return request({
    url: api.login,
    method: 'post',
    data: param
  })
}

export function changePassword(param: changePasswordParam):Promise<AxiosResponse<IResponse<string>>> {
  return request({
    url: api.changePassword,
    method: 'post',
    data: param
  })
}