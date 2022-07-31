import request from '/@/utils/request'
import { AxiosResponse } from 'axios'
import { GLOBAL_FRONTENT_URL_PREFIX } from '/@/constant'
const api = {
  detail: `${GLOBAL_FRONTENT_URL_PREFIX}/firewall/detail`,
  save: `${GLOBAL_FRONTENT_URL_PREFIX}/firewall/save`
}

export interface firewallDetail {
    status: string,
    mode: string,
    ips: string
}

export function loadFirewallDetail():Promise<AxiosResponse<IResponse<firewallDetail>>> {
  return request({
    url: api.detail,
    method: 'get'
  })
}

export function saveFirewallDetail(param: firewallDetail):Promise<AxiosResponse<IResponse<unknown>>> {
  return request({
    url: api.save,
    method: 'post',
    params: param
  })
}