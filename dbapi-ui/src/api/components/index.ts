import request from '/@/utils/request'
import { AxiosResponse } from 'axios'
import { GLOBAL_FRONTENT_URL_PREFIX } from '/@/constant'

const api = {
  getTableList: `${GLOBAL_FRONTENT_URL_PREFIX}/getTableList`
}
export type ITag = '所有' | '家' | '公司' | '学校' | '超市'
export interface ITableList {
    page: number
    size: number
    tag: ITag
}
export function getTableList(tableList: ITableList): Promise<AxiosResponse<IResponse>> {
  return request({
    url: api.getTableList,
    method: 'get',
    params: tableList
  })
}