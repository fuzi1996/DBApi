<template>
  <el-form
    ref='apiFormRef'
    :model='apiForm'
    :rules='rules'
    label-width='120px'
    size='default'
    status-icon
  >
    <el-card shadow='hover'>
      <template #header>
        <span>{{ $t("m.basic_info") }}</span>
      </template>
      <el-form-item
        :label='$t("m.name")'
        prop='name'
      >
        <el-input v-model='apiForm.name' />
      </el-form-item>
      <el-form-item
        :label='$t("m.path")'
        prop='path'
      >
        <el-input v-model='apiForm.path'>
          <template #prepend>
            http://{{ localpath }}
          </template>
        </el-input>
      </el-form-item>
      <el-form-item
        label='API分组'
        prop='groupId'
      >
        <el-select
          v-model='apiForm.groupId'
          placeholder='请选择API分组'
          class='select-item'
        >
          <el-option
            v-for='item in dynamicTags'
            :key='item.id'
            :label='item.name'
            :value='item.id'
          />
        </el-select>
      </el-form-item>
      <el-form-item
        label='描述'
      >
        <el-input
          v-model='apiForm.note'
          type='textarea'
        />
      </el-form-item>
    </el-card>
    <el-card shadow='hover'>
      <el-form-item
        label='SQL'
      >
        <SqlConfig
          ref='sqlConfigRef'
          @datasourceid-change='handleDataSourceChange'
        />
      </el-form-item>
    </el-card>
    <el-card shadow='hover'>
      <el-form-item
        label='Content-Type'
        prop='contentType'
      >
        <el-select
          v-model='apiForm.contentType'
          class='select-item-with-tip'
        >
          <el-option
            v-for='item in CONTENT_TYPE_OPTIONS'
            :key='item'
            :label='item'
            :value='item'
          />
        </el-select>
        <el-tooltip
          placement='top-start'
          effect='dark'
        >
          <template #content>
            <p>
              对于application/x-www-form-urlencoded类型的API，用户在请求该API的时候既可以使用application/x-www-form-urlencoded，也可以使用application/json</p>
            <p>对于application/json类型的API，用户在请求该API的时候只能使用application/json</p>
          </template>
          <el-icon class='info-filled'>
            <InfoFilled />
          </el-icon>
        </el-tooltip>
      </el-form-item>

      <el-form-item>
        <template #label>
          <span v-if='apiForm.contentType == CONTENT_TYPE.FORM_URLENCODED'>请求参数</span>
          <span v-if='apiForm.contentType == CONTENT_TYPE.JSON'>请求参数示例</span>
        </template>
        <div
          v-if='apiForm.contentType == CONTENT_TYPE.FORM_URLENCODED'
          class='width-100'
        >
          <el-table
            :data='apiForm.formParam'
            border
            stripe
            max-height='700'
            size='mini'
            empty-text='暂无参数'
          >
            <el-table-column
              prop='name'
              label='参数名称'
              width='220px'
            >
              <template #default='scope'>
                <el-input v-model='scope.row.name' />
              </template>
            </el-table-column>
            <el-table-column
              label='参数类型'
              width='220px'
            >
              <template #default='scope'>
                <el-select
                  v-model='scope.row.type'
                  :options='options'
                >
                  <el-option
                    v-for='item in DATA_TYPE_OPTIONS'
                    :key='item.value'
                    :label='item.label'
                    :value='item.value'
                  />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column
              label='参数说明'
            >
              <template #default='scope'>
                <el-input v-model='scope.row.note' />
              </template>
            </el-table-column>
            <el-table-column
              label='操作'
              width='100px'
            >
              <template #default='scope'>
                <el-button
                  circle
                  type='danger'
                  icon='el-icon-delete'
                  size='small'
                  @click='deleteRow(scope.$index)'
                />
              </template>
            </el-table-column>
          </el-table>
          <el-button
            icon='el-icon-plus'
            type='primary'
            circle
            size='small'
            @click='addRow'
          />
        </div>
        <div
          v-if='apiForm.contentType == CONTENT_TYPE.JSON'
          class='width-98'
        >
          <JsonParam
            v-model='apiForm.jsonParam'
            :code-mirror-style='jsonParamTextareaItem'
          />
        </div>
        <el-tooltip
          v-if='apiForm.contentType == CONTENT_TYPE.JSON'
          placement='top-start'
          effect='dark'
        >
          <template #content>
            对于application/json类型的API，这个参数示例仅用来生成接口文档，方便调用API的用户查看接口的json参数格式
          </template>
          <el-icon class='info-filled json-param-tooltip'>
            <InfoFilled />
          </el-icon>
        </el-tooltip>
      </el-form-item>

      <el-form-item label='权限'>
        <el-radio-group v-model='apiForm.previlege'>
          <el-radio :label='PREVILEGE.PRIVATE'>{{ $t('m.private') }}</el-radio>
          <el-radio :label='PREVILEGE.PUBLIC'>{{ $t('m.public') }}</el-radio>
        </el-radio-group>

        <el-tooltip
          placement='top-start'
          effect='dark'
        >
          <template #content>
            {{ $t('m.access_tip') }}
          </template>
          <el-icon class='info-filled'>
            <InfoFilled />
          </el-icon>
        </el-tooltip>
      </el-form-item>
    </el-card>
  </el-form>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, defineProps,defineExpose,toRefs,watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { IApiBasicConfigEditItem,getIPAndPort,parseParam,IParamItem,IApiSqlItem } from '/@/api/api/api'
import { listAllGroup,IGroupItem } from '/@/api/api/group'
import { CONTENT_TYPE,PREVILEGE,DATA_TYPE,DATA_TYPE_OPTIONS } from '/@/constant/common'
// import { SQL_PARAM_CACHE_ITEM } from '/@/type/views/apiComponent'
import JsonParam from './jsonParam.vue'
import SqlConfig from './sqlConfig.vue'
import { useSqlStore } from '/@/store/modules/sql'

const jsonParamTextareaItem = {
  width: '100%',
  height: '200px'
}

const { getSqlList,getSqlListByIndex,addSqlItem,resetSqlList,removeSqlListByIndex,resetSqlListLabel } = useSqlStore()
const CONTENT_TYPE_OPTIONS = Object.keys(CONTENT_TYPE).map(item => CONTENT_TYPE[item])
// const SQL_PARAM_CACHE:SQL_PARAM_CACHE_ITEM = {}
const localpath = ref<string>('')
const apiFormRef = ref<FormInstance>()
const sqlConfigRef = ref<any>(null)
const dynamicTags = ref<IGroupItem[]>([])

let apiForm = reactive<IApiBasicConfigEditItem>({ 
  id: '',
  name: '',
  note: '',
  path: '',
  datasourceId: '',
  sqlList: [],
  params: '',
  status: 0,
  previlege: 1,
  groupId: '',
  createTime: '',
  updateTime: '',
  contentType: CONTENT_TYPE.JSON,
  jsonParam: '{}',
  formParam: []
})


const collectData = () => {
  return apiForm
}

const setData = (params:IApiBasicConfigEditItem) => {
  apiForm = Object.assign(apiForm,params)
  if(params) {
    if(params) {
      apiForm.formParam.splice(0)
      try{
        apiForm.formParam.push(...JSON.parse(params.params))
      }catch(error) {
        console.warn('无法将',params.params,'格式化为JSON对象')
      }
    }
  }

  resetSqlList(apiForm.sqlList)
  sqlConfigRef.value.setDataSourceId(apiForm.datasourceId)
}


defineExpose({
  collectData,
  setData
})

const rules = reactive<FormRules>({
  name: [
    { required: true, message: '请输入名称', trigger: 'blur' }
    // { min: 3, max: 5, message: 'Length should be 3 to 5', trigger: 'blur' }
  ],
  path: [
    {
      required: true,
      message: '请输入请求路径',
      trigger: 'blur'
    }
  ],
  groupId: [
    {
      required: true,
      message: '请选择API分组',
      trigger: 'blur'
    }
  ],
  contentType: [
    {
      required: true,
      message: '请选择Content-Type',
      trigger: 'blur'
    }
  ]
})

const addRow = () => {
  apiForm.formParam.push({ name:'',type: DATA_TYPE.STRING,note:'' })
}

const deleteRow = (index:number) => {
  apiForm.formParam.splice(index,1)
}

const loadAllGroup = () => {
  listAllGroup().then(({ data }) => {
    dynamicTags.value.splice(0)
    dynamicTags.value.push(...data.data)
  })
}

onMounted(() => {
  getIPAndPort().then(path => {
    localpath.value = path
  })
  loadAllGroup()
})

const submitForm = async(formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      console.log('submit!')
    } else {
      console.log('error submit!', fields)
    }
  })
}

const resetForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.resetFields()
}

const handleDataSourceChange = (id:string) => {
  apiForm.datasourceId = id
}

</script>
<style scoped>
.select-item{
  width: 100%;
}
.select-item-with-tip{
  width: 98%;
}
.info-filled{
  margin-left: 5px;
}
.width-98{
  width: 98%;
}
.width-100{
  width: 100%;
}
.json-param-tooltip{
  top: -85px;
  position: relative;
}
</style>
