<template>
  <el-tabs
    tab-position='top'
    type='border-card'
    @tab-click='handleTabClick'
  >
    <el-tab-pane
      label='接口测试'
    >
      <div class='mycontent'>
        <h4>{{ $t('m.url') }}：</h4>
        <el-input v-model='url' />

        <el-alert
          v-show='getMode == "cluster"'
          type='warning'
          show-icon
          title='如果是外网访问请将网关地址设置为外网IP端口'
          style='margin-top: 10px;'
        />
        <h4>Header：</h4>

        <el-form
          label-width='2000px'
          style='width: 650px'
        >
          <el-form-item label='Content-Type'>
            <el-input
              v-model='contentType'
              disabled
            />
          </el-form-item>
          <el-form-item
            v-show='previlege == PREVILEGE.PRIVATE'
            label='token'
          >
            <el-input v-model='token' />
          </el-form-item>
        </el-form>

        <h4>{{ $t('m.parameters') }}：</h4>
        <div class='textarea'>
          <el-input
            v-show='contentType === CONTENT_TYPE.JSON'
            v-model='jsonParam'
            placeholder='填写json参数'
            type='textarea'
            rows='10'
          />
        </div>
        <el-form
          v-show='contentType === CONTENT_TYPE.FORM_URLENCODED'
          label-width='200px'
        >
          <el-form-item
            v-for='(item,index) in params'
            :key='item.id'
            style='margin-bottom: 5px'
          >
            <template #label>
              <data-tag
                :name='item.name'
                :type='item.type'
              />
            </template>
            <el-input
              v-if='!item.type.startsWith("Array")'
              v-model='item.value'
              :placeholder='item.note'
              class='input-value'
            />
            
            <template
              v-for='(childItem,childIndex) in item.values'
              :key='childIndex'
            >
              <div class='param-value-container'>
                <el-input
                  v-model='childItem.va'
                  :placeholder='childItem.note'
                  class='param-value'
                />
                <el-button
                  v-if='childIndex === 0'
                  icon='el-icon-plus'
                  type='primary'
                  circle
                  size='small'
                  @click='addRow(index)'
                />
                <el-button
                  icon='el-icon-delete'
                  type='danger'
                  circle
                  size='small'
                  @click='deleteRow(index,childIndex)'
                />
              </div>
              
            </template>
          </el-form-item>

        </el-form>
        <div class='btn-container'>
          <el-button @click='handleRequest'>{{ $t('m.send') }}</el-button>
        </div>

        <h4>{{ $t('m.result') }}：</h4>

        <el-table
          v-show='showTable'
          :data='tableData'
          size='mini'
          border
          stripe
          max-height='700'
        >
          <el-table-column
            v-for='item in keys'
            :key='item'
            :prop='item'
            :label='item'
          />
        </el-table>
        <JsonParam
          v-if='!showTable'
          v-model='response'
          :code-mirror-style='jsonParamTextareaItem'
        />
      </div>
    </el-tab-pane>
    <el-tab-pane
      label='调用示例'
    >
      <call-example
        ref='callExampleRef'
        :address='url'
        :detail='{path,params,previlege,jsonParam,contentType,token}'
      />
    </el-tab-pane>
    <el-tab-pane label='申请Token'>
      <div class='token-container'>
        <el-input v-model='tokenUrl'>
          <template #append>
            <el-button
              icon='el-icon-caret-right'
              @click='getToken'
            />
          </template>
        </el-input>
      </div>
    </el-tab-pane>
  </el-tabs>
</template>
<script lang="ts">
import { defineComponent,ref,reactive,onMounted,nextTick } from 'vue'
import { IApiConfigItem,IApiBasicConfigEditItem,getIPAndPort,getIP,parseParam,IParamItem,IApiSqlItem,loadApi,updateApi,addApi } from '/@/api/api/api'
import { CONTENT_TYPE,PREVILEGE,DATA_TYPE,DATA_TYPE_OPTIONS } from '/@/constant/common'
import ApiAdvanceConfig from './components/apiAdvanceConfig.vue'
import ApiBasicConfig from './components/apiBasicConfig.vue'
import { useSqlStore } from '/@/store/modules/sql'
import { useRouter,Router } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useLayoutStore } from '/@/store/modules/layout'
import DataTag from './components/dataTag.vue'
import axios from 'axios'
import JsonParam from './components/jsonParam.vue'
import CallExample from './components/callExample.vue'
import type { TabsPaneContext } from 'element-plus'

export default defineComponent({
  components:{
    DataTag,
    JsonParam,
    CallExample
  },
  setup() {
    const jsonParamTextareaItem = {
      width: '100%',
      height: '200px'
    }
    const { removeActiveTag,getMode } = useLayoutStore()
    const { getSqlList,getSqlListByIndex,addSqlItem,resetSqlList,removeSqlListByIndex,resetSqlListLabel,clearSqlList } = useSqlStore()
    
    const callExampleRef = ref<any>(null)
    const router:Router = useRouter()

    const path = ref<string>('')
    const previlege = ref<number>(PREVILEGE.PRIVATE)
    const params = ref<any[]>([])
    const address = ref<string>('')
    const tokenUrl = ref<string>('')
    const url = ref<string>('')
    const contentType = ref<string>('')
    const jsonParam = ref<string>('')
    const token = ref<string>('')
    const response = ref<any>('')
    const showTable = ref<boolean>(false)

    const api = reactive({})
    
    const keys = ref<any>([])
    const tableData = ref<any>([])

    const request = axios.create({
      // API 请求的默认前缀
      baseURL: import.meta.env.VUE_APP_API_BASE_URL as string | undefined,
      timeout: 60000 // 请求超时时间
    })
    

    getIPAndPort().then((data:string) => {
      address.value = data
      url.value = `http://${address.value}/${path.value}`
      tokenUrl.value = `http://${address.value}/token/generate?appid=xxx&secret=xxx`
    })
    getIP().then((ipPort:string) => {
      tokenUrl.value = `http://${ipPort}/token/generate?appid=xxx&secret=xxx`
    })
    const addRow = (index:number) => {
      params.value[index].values.push({ va: null })
    }
    const deleteRow = (index:number, childIndex:number) => {
      params.value[index].values.splice(childIndex, 1)
    }
    const getToken = () => {
      request(tokenUrl.value).then(response => {
        const { data,success,msg } = response.data
        if(!success) {
          ElMessage.error(msg)
        }else{
          token.value = data.token
          ElMessage.success('Token获取成功')
        }
      })
        .catch(error => {
          console.error('请求token出错',error)
        })
    }
    const handleRequest = () => {
      response.value = ''
      let requestParam:any = {}
      if (contentType.value === CONTENT_TYPE.FORM_URLENCODED) {
        params.value.forEach((param) => {
          // 构造数组类型的请求参数
          if (param.type.startsWith('Array')) {
            const values = param.values.map((item:any) => item.va)
            requestParam[param.name] = values
          } else {
            requestParam[param.name] = param.value
          }
        })
      } else if (contentType.value === CONTENT_TYPE.JSON) {
        requestParam = jsonParam.value
      }
      request.post(url.value, requestParam, {
        headers: {
          'Content-Type': contentType.value,
          Authorization: token.value ? '' : token.value
        }
      })
        .then((res) => {
          console.log('res',res)
          if (res.status == 200) {
            showTable.value = false
            response.value = JSON.stringify(res.data)
            format()
          }
        })
        .catch((error) => {
          console.error('请求失败',error)
          // 只要状态码不是200都会进入catch逻辑
          ElMessage.error(error.response.data.msg)
        })
    }
    const format = () => {
      const o = JSON.parse(response.value)
      response.value = JSON.stringify(o, null, 4)
    }
    onMounted(() => {
      const currentRoute = router.currentRoute.value
      if(currentRoute.query.id) {
        loadApi(currentRoute.query.id as string).then(data => {
          path.value = data.path
          previlege.value = data.previlege

          if(data.params) {
            let paramsTemp:any[] = JSON.parse(data.params)
            paramsTemp.forEach((item:any) => {
              if (item.type.startsWith('Array')) {
                item.values = [{ va: '' }]
              }
            })
            params.value.splice(0)
            params.value.push(...paramsTemp)
          }

          url.value = `http://${address.value}/${path.value}`
          contentType.value = data.contentType
          jsonParam.value = data.jsonParam
        })
      }
    })

    const handleTabClick = (pane: TabsPaneContext) => {
      if (pane.paneName === '1') {
        nextTick(() => {
          callExampleRef.value.refresh()
        })
      }
    }

    return {
      CONTENT_TYPE,
      PREVILEGE,
      getMode,
      path,
      previlege,
      params,
      address,
      tokenUrl,
      url,
      contentType,
      jsonParam,
      token,
      response,
      tableData,
      showTable,
      format,
      addRow,
      deleteRow,
      getToken,
      handleRequest,
      jsonParamTextareaItem,
      handleTabClick,
      keys,
      callExampleRef
    }
  }
})
</script>
<style scoped>
.input-value{
  width: 80%;
}

.param-value-container{
  margin-bottom: 15px;
  margin-right: 15px;
  width: 40%;
}

.param-value{
  width: 400px;
  margin-right: 5px;
}

.btn-container{
  width: 100%;
  text-align: right;
}
</style>