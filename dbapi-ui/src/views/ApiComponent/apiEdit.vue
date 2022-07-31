<template>
  <div>
    <el-tabs
      type='border-card'
    >
      <el-tab-pane label='基础配置'>
        <ApiBasicConfig
          ref='basicRef'
        />
      </el-tab-pane>
      <el-tab-pane label='高级配置'>
        <ApiAdvanceConfig
          ref='advanceRef'
        />
      </el-tab-pane>
    </el-tabs>
    <el-button
      class='button'
      type='primary'
      @click='handleBtnSaveClick'
    >
      {{ readOnly?'确定':'保存' }}
    </el-button>
  </div>
</template>

<script lang="ts" setup>
import { ref,reactive,onMounted } from 'vue'
import { IApiConfigItem,IApiConfigSaveParam,getIPAndPort,parseParam,IParamItem,IApiSqlItem,loadApi,updateApi,addApi } from '/@/api/api/api'
import { CONTENT_TYPE,PREVILEGE,DATA_TYPE,DATA_TYPE_OPTIONS } from '/@/constant/common'
import ApiAdvanceConfig from './components/apiAdvanceConfig.vue'
import ApiBasicConfig from './components/apiBasicConfig.vue'
import { useSqlStore } from '/@/store/modules/sql'
import { useRouter,Router } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useLayoutStore } from '/@/store/modules/layout'

const { removeActiveTag } = useLayoutStore()
const { getSqlList,getSqlListByIndex,addSqlItem,resetSqlList,removeSqlListByIndex,resetSqlListLabel,clearSqlList } = useSqlStore()
const basicRef = ref<any>(null)
const advanceRef = ref<any>(null)
const router:Router = useRouter()

let apiForm = reactive<IApiConfigItem>({
  id: '',
  name: '',
  note: '',
  path: '',
  datasourceId: '',
  sqlList: [
    {
      id: '',
      apiId: '',
      label: '',
      sqlText: '-- 请输入sql，一个标签只能输入一条sql',
      transformPlugin: '',
      transformPluginParams: '' 
    }
  ],
  params: '',
  status: 0,
  previlege: 1,
  groupId: '',
  createTime: '',
  updateTime: '',
  contentType: CONTENT_TYPE.JSON,
  jsonParam: '{}',

  cachePlugin: '',
  cachePluginParams: '',
  openTrans: 0,
  alarmPlugin: '',
  alarmPluginParam: ''
})
const readOnly = ref<boolean>(false)

onMounted(() => {
  const currentRoute = router.currentRoute.value
  clearSqlList()
  if(currentRoute.query.id) {
    loadApi(currentRoute.query.id as string).then(data => {
      apiForm = Object.assign(apiForm,data)
      basicRef.value.setData(apiForm)
      advanceRef.value.setData(apiForm)
    })
    if(currentRoute.query.readOnly) {
      readOnly.value = !!currentRoute.query.readOnly
    }
  }else{
    addSqlItem()
  }
})

const saveCallback = (data:IResponse<any>) => {
  if(!data.success) {
    ElMessage.error(data.msg)
  }else{
    clearSqlList()
    ElMessage.success('保存成功')
    router.push({ name:'ApiList' })
    removeActiveTag()
  }
}

const doSave = () => {
  const basicData = basicRef.value.collectData()
  const advanceData = advanceRef.value.collectData()
  const data:IApiConfigSaveParam = {
    ...basicData,
    ...advanceData,
    sqlList: getSqlList
  }

  if(Array.isArray(data.formParam) && data.formParam.length > 0) {
    data.params = JSON.stringify(data.formParam)
  }

  if(data.id) {
    updateApi(data).then(saveCallback)
  }else{
    addApi(data).then(saveCallback)
  }
}

const handleBtnSaveClick = () => {
  if(readOnly.value) {
    clearSqlList()
    router.push({ name:'ApiList' })
    removeActiveTag()
  }else{
    doSave()
  }
}

</script>
<style scoped>
.button {
  position:absolute;
  right: 5px;
  top: 5px;
}
</style>