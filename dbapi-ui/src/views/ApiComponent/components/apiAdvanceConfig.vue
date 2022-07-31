<template>
  <el-form
    ref='apiFormRef'
    :model='apiForm'
    :rules='rules'
    label-width='120px'
    size='default'
    status-icon
  >
    <el-card>
      <template #header>
        <span>高级配置</span>
      </template>
      <el-form-item label='事务'>
        <el-radio-group v-model='apiForm.openTrans'>
          <el-radio
            v-for='item in DB_TRANSACTION_MODE' 
            :key='item.value'
            :label='item.value'
          >
            {{ item.label }}
          </el-radio>
        </el-radio-group>
        <el-tooltip
          placement='top-start'
          effect='dark'
        >
          <template #content>
            开启事务后，如果有多条SQL，多条SQL将在同一个事务内执行
          </template>
          <el-icon class='info-filled'>
            <InfoFilled />
          </el-icon>
        </el-tooltip>
        <el-alert
          v-show='apiForm.openTrans == 1'
          type='warning'
          show-icon
        >
          <p>注意如果是hive等不支持事务的数据库，不要开启事务</p>
        </el-alert>
      </el-form-item>

      <el-form-item :label='$t("m.data_convert")'>
        <div
          v-for='(item,index) in getSqlList'
          :key='item.id'
          class='sql-list'
        >
          <el-card
            class='box-card'
            shadow='hover'
          >
            <template #header>
              <div class='card-header'>
                <h2>sql-{{ item.label }}</h2>
              </div>
            </template>
            <div class='trans-plugin-class'>
              <span class='label'>插件类名</span>
              <el-select
                v-model='item.transformPlugin'
                class='trans-config-item'
                placeholder='请选择数据转换插件java类名'
                clearable
                no-data-text='暂无插件'
                @clear='clearTransPluginParam(index)'
              >

                <el-option
                  v-for='op in transformPluginOptions'
                  :key='op.className'
                  :value='op.className'
                  :label='op.className'
                >
                  <div>
                    <el-tooltip
                      placement='top-start'
                      effect='dark'
                    >
                      <template #content>
                        <div>插件描述：{{ op.description }}</div>
                        <div>插件参数描述：{{ op.paramDescription }}</div>
                      </template>
                      <div>
                        <span>{{ op.className }}</span>
                        <span style='color: #cccccc;margin-left:10px;'>{{ op.name }} </span>
                      </div>
                    </el-tooltip>
                  </div>
                </el-option>
              </el-select>
            </div>
            <div class='trans-plugin-param'>
              <span class='label'>插件参数</span>
              <el-input
                v-model='item.transformPluginParams'
                placeholder='请输入数据转换插件参数'
                class='trans-config-item'
              />
            </div>
          </el-card>
        </div>
        <el-alert
          type='warning'
          show-icon
          class='trans-alert-item'
        >
          填写“插件类名”表示对sql执行结果开启数据转换功能，不填写表示不转换。
          如果有多条sql，每个sql对应一个数据转换插件
        </el-alert>

      </el-form-item>

      <el-form-item :label='$t("m.cache")'>
        <div class='plugin-class'>
          <span class='label'>插件类名</span>
          <el-select
            v-model='apiForm.cachePlugin'
            class='config-item'
            placeholder='请选择缓存插件java类名'
            clearable
            no-data-text='暂无插件'
            @clear='clearCachePluginParam()'
          >
            <el-option
              v-for='item in cachePluginOptions'
              :key='item.className'
              :value='item.className'
            >
              <div>
                <el-tooltip
                  placement='top-start'
                  effect='dark'
                >
                  <template #content>
                    <div>插件描述：{{ item.description }}</div>
                    <div>插件参数描述：{{ item.paramDescription }}</div>
                  </template>
                  <div>
                    <span>{{ item.className }}</span>
                    <span style='color: #cccccc;margin-left:10px;'>{{ item.name }} </span>
                  </div>
                </el-tooltip>
              </div>

            </el-option>
          </el-select>
        </div>
        <div class='plugin-param'>
          <span class='label'>插件参数</span>
          <el-input
            v-model='apiForm.cachePluginParams'
            placeholder='请输入缓存插件参数'
            class='config-item'
          />
        </div>
        <el-alert
          type='warning'
          show-icon
          class='alert-item'
        >
          填写“插件类名”表示对结果数据开启缓存，不填写表示不开启缓存
        </el-alert>

      </el-form-item>

      <el-form-item label='失败告警'>
        <div class='plugin-class'>
          <span class='label'>插件类名</span>
          <el-select
            v-model='apiForm.alarmPlugin'
            class='config-item'
            placeholder='请选择告警插件java类名'
            clearable
            no-data-text='暂无插件'
            @clear='clearAlarmPluginParam()'
          >
            <el-option
              v-for='item in alarmPluginOptions'
              :key='item.className'
              :value='item.className'
              :label='item.className'
            >
              <div>
                <el-tooltip
                  placement='top-start'
                  effect='dark'
                >
                  <template #content>
                    <div>插件描述：{{ item.description }}</div>
                    <div>插件参数描述：{{ item.paramDescription }}</div>
                  </template>
                  <div>
                    <span>{{ item.className }}</span>
                    <span style='color: #cccccc;margin-left:10px;'>{{ item.name }} </span>
                  </div>
                </el-tooltip>
              </div>
            </el-option>
          </el-select>
        </div>
        <div class='plugin-param'>
          <span class='label'>插件参数</span>

          <el-input
            v-model='apiForm.alarmPluginParam'
            class='config-item'
          />
        </div>
        
        <el-alert
          type='warning'
          show-icon
          class='alert-item'
        >
          填写“插件类名”表示对API失败告警，不填写表示失败不告警
        </el-alert>
      </el-form-item>

      <div>
        <a
          target='_blank'
          href='https://gitee.com/freakchicken/db-api/blob/dev/dbapi-assembly/docs/instruction.md#%E6%8F%92%E4%BB%B6'
        >
          <el-icon><InfoFilled /></el-icon>
          {{
            $t('m.what_is_plugin')
          }}
        </a>
        <a
          class='info-filled'
          target='_blank'
          href='https://gitee.com/freakchicken/db-api/blob/dev/dbapi-assembly/docs/plugin%20development.md#242-%E5%B1%80%E9%83%A8%E5%8F%82%E6%95%B0'
        >
          <el-icon><InfoFilled /></el-icon>
          {{
            $t('m.what_is_plugin_param')
          }}
        </a>
      </div>
    </el-card>
  </el-form>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, defineProps,defineExpose } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { IApiBasicConfigEditItem,IApiAdvanceConfig,getIPAndPort,parseParam,IParamItem,IApiSqlItem } from '/@/api/api/api'
import { getAllPlugin,IAllPlugin,IPlugin } from '/@/api/api/plugin'
import { listAllGroup,IGroupItem } from '/@/api/api/group'
import { CONTENT_TYPE,PREVILEGE,DATA_TYPE,DATA_TYPE_OPTIONS } from '/@/constant/common'
import { DB_TRANSACTION_MODE } from '/@/constant/dataSource'
// import { SQL_PARAM_CACHE_ITEM } from '/@/type/views/apiComponent'
import JsonParam from './jsonParam.vue'
import { useSqlStore } from '/@/store/modules/sql'


const { getSqlList,getSqlListByIndex,addSqlItem,resetSqlList,removeSqlListByIndex,resetSqlListLabel } = useSqlStore()

const jsonParamTextareaItem = {
  width: '100%',
  height: '200px'
}


const CONTENT_TYPE_OPTIONS = Object.keys(CONTENT_TYPE).map(item => CONTENT_TYPE[item])
// const SQL_PARAM_CACHE:SQL_PARAM_CACHE_ITEM = {}
const localpath = ref<string>('')
const apiFormRef = ref<FormInstance>()
const dynamicTags = ref<IGroupItem[]>([])
const cachePluginOptions = ref<IPlugin[]>([])
const transformPluginOptions = ref<IPlugin[]>([])
const alarmPluginOptions = ref<IPlugin[]>([])

let apiForm = reactive<IApiAdvanceConfig>({
  cachePlugin: '',
  cachePluginParams: '',
  openTrans: 0,
  alarmPlugin: '',
  alarmPluginParam: ''
})


const collectData = () => {
  return apiForm
}

const setData = (params:IApiAdvanceConfig) => {
  apiForm.cachePlugin = params.cachePlugin
  apiForm.cachePluginParams = params.cachePluginParams
  apiForm.openTrans = params.openTrans
  apiForm.alarmPlugin = params.alarmPlugin
  apiForm.alarmPluginParam = params.alarmPluginParam
}

defineExpose({
  collectData,
  setData
})

onMounted(() => {
  getAllPlugin().then(data => {
    cachePluginOptions.value.splice(0)
    cachePluginOptions.value.push(...data.cache)

    alarmPluginOptions.value.splice(0)
    alarmPluginOptions.value.push(...data.alarm)

    transformPluginOptions.value.splice(0)
    transformPluginOptions.value.push(...data.transform)
  })
})

const rules = reactive<FormRules>({})


</script>
<style scoped>
.info-filled{
  margin-left: 5px;
}
.sql-list{
  width:99%;
}
.label {
  margin: 0 5px 0 15px;
}
.alert-item{
  width:99%;
}
.config-item{
  width:92%;
}
.plugin-class{
  width:100%;
}
.plugin-param{
  width:100%;
  margin-top: 15px;
  margin-bottom: 15px;
}
.trans-plugin-class{
  width:100%;
}
.trans-plugin-param{
  width:100%;
  margin-top: 15px;
}
.trans-alert-item{
  width:99%;
  margin-top: 15px;
}
.trans-config-item{
  width:92%;
}
</style>
