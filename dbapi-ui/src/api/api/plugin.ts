import request from '/@/utils/request'
import { AxiosResponse } from 'axios'
import { GLOBAL_FRONTENT_URL_PREFIX } from '/@/constant'

const api = {
  listAllPlugin: `${GLOBAL_FRONTENT_URL_PREFIX}/plugin/all`
}

export interface IPlugin {
    name: string,
    description: string,
    className: string,
    paramDescription: string
}

export interface IAllPlugin{
    cache: IPlugin[]
    transform: IPlugin[]
    alarm: IPlugin[]
}

export async function getAllPlugin():Promise<IAllPlugin> {
  const res = await request.post<IResponse<IAllPlugin>>(api.listAllPlugin)
  return res.data.data
}