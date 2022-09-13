import { useLayoutStore } from '/@/store/modules/layout'
import axios from 'axios'
import { AxiosResponse,AxiosError } from 'axios'
import { ElLoading, ElNotification } from 'element-plus'
import { CONTENT_TYPE } from '/@/constant/common'
import qs from 'qs'

let loading:{close():void}
// 创建 axios 实例
const request = axios.create({
  // API 请求的默认前缀
  baseURL: import.meta.env.VUE_APP_API_BASE_URL as string | undefined,
  timeout: 60000, // 请求超时时间
  headers: {
    'Content-Type': CONTENT_TYPE.FORM_URLENCODED
  }
})

const responseErrorHandler = (response:AxiosResponse) => {
  const { data,status } = response
  const { getStatus, logout } = useLayoutStore()
  if(status !== 200) {
    let title = '请求失败'
    if(status === 401) {
      if (getStatus.ACCESS_TOKEN) {
        logout()
      }
      title = '身份认证失败'
    }
    ElNotification({
      title,
      message:data.msg,
      type: 'error'
    })
    return Promise.reject(new Error(data.msg || 'Error'))
  }
}

const requestErrorHandler = (error:AxiosError) => {
  ElNotification({
    title: '请求失败',
    message: error.message,
    type: 'error'
  })
  return Promise.reject(error)
}

// 异常拦截处理器
const errorHandler = (error:AxiosError) => {
  loading.close()
  const { response } = error
  if(response) {
    return responseErrorHandler(response)
  }else{
    return requestErrorHandler(error)
  }
}

// request interceptor
request.interceptors.request.use(config => {
  const { getStatus } = useLayoutStore()
  loading = ElLoading.service({
    lock: true,
    text: 'Loading',
    spinner: 'el-icon-loading',
    background: 'rgba(0, 0, 0, 0.4)'
  })
  const token = getStatus.ACCESS_TOKEN
  // 如果 token 存在
  // 让每个请求携带自定义 token 请根据实际情况自行修改
  if (token) {
    config.headers['Authorization'] = token
  }
  // form表单提交multipart/form-data的时候，不需要序列化参数
  if (config.method === 'post' && config.headers['Content-Type'] === CONTENT_TYPE.FORM_URLENCODED) {
    config.data = qs.stringify(config.data, { indices: false })
  }
  return config
}, errorHandler)

// response interceptor
request.interceptors.response.use((response:AxiosResponse<IResponse>) => {
  loading.close()
  return response
}, errorHandler)

export default request