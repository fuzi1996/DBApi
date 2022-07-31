<template>
  <div :class='["root", isFullScreen?"full":""]'>
    <div class='left'>
      <div>
        <DataSourceSelect
          v-model='dataSourceId'
          :options='dataSourceList'
          :nullable='false'
          :label='$t("m.datasource")'
          size='mini'
          width='176px'
          option-label='name'
          option-value='id'
          @onchange='handleDataSourceChange'
        />
      </div>
      <div class='bottom'>
        <div
          v-for='(item,index) in tableList'
          :key='item.label'
        >
          <div>
            <div
              class='table'
              @click.prevent='handleTableNameClick(item.label,index)'
            >
              <i class='iconfont icon-table' />{{ item.label }}
            </div>
            <div v-show='item.showColumns'>
              <div
                v-for='(it) in item.columns'
                :key='it.label'
                class='column'
              >
                <span class='columnName'>{{ it.label }}</span>
                <span class='columnType'>{{ it.typeName }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class='right'>
      <div class='top'>
        <div class='tool'>
          <div v-show='isFullScreen'>
            <div
              class='item'
              @click='executeCurrentSql'
            ><i class='iconfont icon-play' /><span>运行SQL</span></div>
            <div
              class='item'
              @click='executeSelectedSql'
            ><i class='iconfont icon-play' /><span>运行选中SQL</span></div>
            <div
              class='item'
              @click='parseSql'
            ><i class='iconfont icon-play' /><span>解析动态SQL</span></div>
            <div
              class='item'
              @click='formatSql'
            ><i class='iconfont icon-play' /><span>格式化SQL</span></div>
          </div>
        </div>
        <div class='quick'>
          <div
            class='tag'
            @click='appendTag("foreach")'
          >foreach</div>
          <div
            class='tag'
            @click='appendTag("if")'
          >if</div>
          <div
            class='tag'
            @click='appendTag("where")'
          >where</div>
          <div
            class='tag'
            @click='appendTag("trim")'
          >trim</div>
          <i
            v-if='mode == "mini"'
            class='iconfont icon-full'
            @click='fullWindow'
          />
          <i
            v-if='mode == "large"'
            class='iconfont icon-mini2'
            @click='miniWindow'
          />
        </div>
      </div>
      <div class='code'>
        <div
          class='multi-sql'
          :style='{"width": !isFullScreen ? "100%" : ""}'
        >
          <SqlCode
            v-for='(item,index) in getSqlList'
            v-show='currentIndex === index'
            :key='item.id'
            :ref='(el) => setSqlCodeRef(el, index)'
            v-model='item.sqlText'
            :code-mirror-style='codeMirrorStyle'
            :mode='mode'
          />

          <div class='tabs'>
            <div
              v-for='(item,index) in getSqlList'
              :key='item.id'
              :class='{"tab":true,"tab-active":currentIndex === index}'
            >
              <div
                class='text'
                @click='handleLabelClick(index, item.sqlText)'
              >
                SQL-{{ item.label }}
              </div>
              <span
                v-if='getSqlList.length > 0'
                class='close'
                @click='handleLabelRemoveClick(index)'
              >
                <el-icon><CircleClose /></el-icon>
              </span>
            </div>
            <div
              class='tab'
              @click='addSqlTab'
            >
              <div class='text'>
                <el-icon><CirclePlus /></el-icon>
                添加
              </div>
            </div>
          </div>
        </div>
        <div class='params'>
          <span>参数设置：</span>
          <el-tooltip
            placement='top-start'
            effect='dark'
          >
            <template #content>
              填写sql运行需要的参数值，拼接成json格式
            </template>
            <el-icon class='info-filled'>
              <InfoFilled />
            </el-icon>
          </el-tooltip>
          <JsonParam
            v-model='params'
            :code-mirror-style='jsonParamCodeMirrorStyle'
          />
        </div>
      </div>
      <div class='result'>
        <div
          v-if='errorMsg != null'
          class='error'
        >
          <el-icon><WarningFilled /></el-icon>
          {{ errorMsg }}
        </div>
        <div
          v-if='updateMsg != null'
          class='updateMsg'
        >
          <el-icon><SuccessFilled /></el-icon>
          {{ updateMsg }}
        </div>
        <div
          v-if='sqlMeta.sql'
          class='sqlMeta'
        >
          <div class='sql'>{{ sqlMeta.sql }}</div>
          <div class='sql'>{{ sqlMeta.jdbcParamValues }}</div>
        </div>
        <div
          v-if='isArrayResult'
          class='table'
        >
          <el-table
            v-if='resultList != null && resultList.length > 0'
            :data='resultList'
            border
            stripe
            style='width: 100%'
            size='small'
          >
            <el-table-column
              v-for='item in Object.keys(resultList[0])'
              :key='item'
              :prop='item'
              :label='item'
            />
          </el-table>
          <div v-if='resultList != null && resultList.length == 0'>
            查询结果为空
          </div>
        </div>
      </div>
    </div>

  </div>
</template>
<script lang="ts">
import { ComponentPublicInstance, HTMLAttributes,ref,reactive,defineComponent } from 'vue'
import { listAllDataSource,IDataSourceBasicMeta,ITableMetaData,getAllTables,getTableColumns } from '/@/api/datasource'
import DataSourceSelect from './dataSourceSelect.vue'
import { IApiBasicConfigEditItem,getIPAndPort,parseParam,executeSql,IParamItem,IApiSqlItem,parseDynamicSql,IParseDynamicSqlResult } from '/@/api/api/api'
import type { PropType } from 'vue'
import SqlCode from './sqlCode.vue'
import { format } from 'sql-formatter'
import JsonParam from './jsonParam.vue'
import { ElMessage } from 'element-plus'
import { useSqlStore } from '/@/store/modules/sql'

interface ITableMetaTreeData extends ITableMetaData{
  showColumns: boolean
}

export default defineComponent({
  name:'SqlConfig',
  components:{
    DataSourceSelect,
    SqlCode,
    JsonParam
  },
  emits:['datasourceid-change'],
  setup(props,context: SetupContext) {
    const sqlCodeRefs: any = {}
    const { getSqlList,getSqlListByIndex,addSqlItem,resetSqlList,removeSqlListByIndex,resetSqlListLabel } = useSqlStore()

    const codeMirrorStyle = {
      height: '100% !important',
      width: '100%'
    }

    const jsonParamCodeMirrorStyle = {
      height: '90%'
    }
    const resultList = ref<any[]>([])
    const isArrayResult = ref<boolean>(false)
    const sqlMeta = reactive<IParseDynamicSqlResult>({
      sql: '',
      jdbcParamValues: []
    })
    const error = ref<string>('')
    const params = ref<string>('{}')
    const mode = ref<string>('mini')
    const errorMsg = ref<string|any>(null)
    const updateMsg = ref<string|any>(null)
    const isFullScreen = ref<boolean>(false)
    // sql序号
    const currentIndex = ref<number>(0)

    const dataSourceId = ref<string>('')
    const dataSourceList = ref<IDataSourceBasicMeta[]>([])

    const tableList = ref<ITableMetaTreeData[]>([])
    

    listAllDataSource().then(data => {
      dataSourceList.value.splice(0)
      dataSourceList.value.push(...data)
    })
    const handleDataSourceChange = (val:string) => {
      context.emit('datasourceid-change',val)
      getAllTables(val).then(data => {
        tableList.value.splice(0)
        const temp = data.map(item => {
          item.showColumns = false
          return item
        }) as ITableMetaTreeData[]
        tableList.value.push(...temp)
      })
    }
    const handleTableNameClick = (tableName:string,index:number) => {
      const element = tableList.value[index]
      if(element.columns.length > 0) {
        element.showColumns = true
      }else{
        getTableColumns(dataSourceId.value,tableName).then(data => {
          element.columns.push(...data)
          element.showColumns = true
        })
      }
    }
    const addSqlTab = () => {
      addSqlItem()
      currentIndex.value = getSqlList.length - 1
    }
    const isShowSqlCode = (index:number) => {
      return currentIndex.value === index
    }
    const handleLabelClick = (index:number) => {
      currentIndex.value = index
    }
    const handleLabelRemoveClick = (index:number) => {
      if(currentIndex.value === index) {
        // 如果删除的就是当前,那就推给前一个
        currentIndex.value = index - 1
      }else if(currentIndex.value > index) {
        currentIndex.value = currentIndex.value - 1
      }
      removeSqlListByIndex(index)
      resetLabel()
    }
    const resetLabel = () => {
      resetSqlListLabel()
    }
    const fullWindow = () => {
      mode.value = 'large'
      isFullScreen.value = true
    }
    const miniWindow = () => {
      mode.value = 'mini'
      isFullScreen.value = false
    }
    const resetSqlExecuteResult = () => {
      resultList.value.splice(0)
      errorMsg.value = null
      updateMsg.value = null
      isArrayResult.value = false
    }
    const executeCurrentSql = () => {
      const { sqlText } = getSqlListByIndex(currentIndex.value)
      doExecuteSql(sqlText)
    }
    const executeSelectedSql = () => {
      const currentSqlCodeInstance = sqlCodeRefs[currentIndex.value]
      const selection = currentSqlCodeInstance.getSelection()
      doExecuteSql(selection)
    }
    const doExecuteSql = (sqlText:string) => {
      resetSqlExecuteResult()
      if(!dataSourceId.value) {
        ElMessage.info('请先选择数据源')
        return
      }
      executeSql(sqlText,dataSourceId.value,params.value).then((data) => {
        setQueryResult(data)
      })
    }
    const setQueryResult = (res:IResponse<any[]>) => {
      if(res.msg) {
        errorMsg.value = res.msg
      }else if(Array.isArray(res.data)) {
        isArrayResult.value = true
        resultList.value.splice(0)
        resultList.value.push(...res.data)
      }else if(typeof res.data === 'string') {
        updateMsg.value = res.data
      }
    }
    const parseSql = () => {
      resetSqlExecuteResult()
      const { sqlText } = getSqlListByIndex(currentIndex.value)
      parseDynamicSql(sqlText,params.value).then(data => {
        if(data.success) {
          sqlMeta.sql = data.data.sql
          sqlMeta.jdbcParamValues = data.data.jdbcParamValues
        }else{
          errorMsg.value = data.msg
        }
      })
    }
    const formatSql = () => {
      const { sqlText } = getSqlListByIndex(currentIndex.value)
      let sql = format(sqlText)
        .replace(/# /g, '#')
        .replace(/{ /g, '{')
        .replace(/ }/g, '}')
        .replace(/< foreach/g, '\n<foreach\n')
        .replace(/< \/ foreach >/g, '\n</foreach>\n')
        .replace(/< if/g, '\n<if')
        .replace(/< \/ if >/g, '\n</if>\n')
        .replace(/<\nwhere\n  >/g, '\n<where>\n')
        .replace(/< \/\nwhere\n  >/g, '\n</where>\n')
        .replace(/< trim/g, '\n<trim')
        .replace(/< \/ trim >/g, '\n</trim>\n')
      const currentSqlCodeInstance = sqlCodeRefs[currentIndex.value]
      currentSqlCodeInstance.setValue(sql)
    }
    const setSqlCodeRef = (el: HTMLElement | ComponentPublicInstance | HTMLAttributes, index:number) => {
      if(el) {
        sqlCodeRefs[index] = el
      }
    }
    const appendTag = (item:string) => {
      let val = ''
      if (item == 'foreach') {
        val = '\n<foreach open="(" close=")" collection="" separator="," item="item" index="index">#{item}</foreach>'
      } else if (item == 'if') {
        val = '\n<if test="" ></if>'
      } else if (item == 'where') {
        val = '\n<where></where>'
      } else if (item == 'trim') {
        val = '\n<trim prefix="" suffix="" suffixesToOverride="" prefixesToOverride=""></trim>'
      }
      const currentSqlCodeInstance = sqlCodeRefs[currentIndex.value]
      currentSqlCodeInstance.setValue(currentSqlCodeInstance.getValue() + val)
    }
    const setDataSourceId = (id:string) => {
      dataSourceId.value = id
    }
    return {
      getSqlList,
      mode,
      error,
      params,
      sqlMeta,
      resultList,
      isArrayResult,
      errorMsg,
      updateMsg,
      isFullScreen,
      dataSourceId,
      dataSourceList,
      tableList,
      currentIndex,
      codeMirrorStyle,
      jsonParamCodeMirrorStyle,
      setDataSourceId,
      appendTag,
      setSqlCodeRef,
      executeCurrentSql,
      executeSelectedSql,
      parseSql,
      formatSql,
      handleTableNameClick,
      handleDataSourceChange,
      addSqlTab,
      isShowSqlCode,
      handleLabelClick,
      handleLabelRemoveClick,
      miniWindow,
      fullWindow
    }
  }
})
</script>
<style scoped lang="scss">
::-webkit-scrollbar {
  /*滚动条整体样式*/
  width: 6px; /*高宽分别对应横竖滚动条的尺寸*/
  height: 1px;
}

::-webkit-scrollbar-thumb {
  /*滚动条里面小方块*/
  border-radius: 6px;
  //box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
  background: #a0a0a0;
}

::-webkit-scrollbar-track {
  /*滚动条里面轨道*/
  //box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
  border-radius: 16px;
  //background: #ededed;
}

.full {
  z-index: 10;
  position: fixed;
  top: 0px;
  left: 0px;
  width: 100vw;
  height: 100vh;

  .left {
    width: 300px !important;
    height: 100vh !important;

    .bottom {
      height: calc(100vh - 30px) !important;
    }
  }

  .right {
    width: 100% !important;
    .code {
      height: calc(100vh - 350px) !important;

      .multi-sql {
        width: calc(100% - 400px) !important;
      }
      
      .params {
        display: block !important;
      }
    }

    .result {
      display: block !important;
    }
  }
}

.root {
  display: flex;
  //height: 430px;

  .left {
    width: 250px !important;
    height: 430px;
    border: 1px solid #999999;
    line-height: 20px;
    background-color: #fff;
    flex-shrink: 0;

    .bottom {
      height: 400px;
      overflow: auto;
      padding-top: 5px;

      .menus {
        position: fixed;
        z-index: 1000;
        background-color: #fff;
        width: 100px;
        height: 40px;
        padding: 10px;
        border: 1px solid #ccc;
        border-radius: 5px;
        box-shadow: 0 0 3px #333;
        line-height: 20px;

        .menu {
          cursor: pointer;

          &:hover {
            background-color: #dedede;
          }
        }
      }

      .table {
        padding-left: 5px;
        font-size: 16px;

        i {
          margin-right: 5px;
          line-height: 20px;
        }

        &:hover {
          background-color: #dedede;
        }
      }

      .column {
        padding-left: 35px;

        .columnType {
          //background-color: #cdf2f6;
          margin-left: 5px;
          padding: 0 3px;
          color: #ccc;
          font-size: 10px;
        }

        .columnName {
          background-color: #eaeaea;
          margin-left: 5px;
          padding: 0 3px;
          font-size: 16px;
        }

        i {
          margin-right: 5px;
          line-height: 20px;
        }

        &:hover {
          background-color: #dedede;
        }
      }
    }
  }

  .right {
    display: block;
    width: calc(100vw - 722px);
    overflow: auto;
    border: 1px solid #999999;
    border-left: 0px;
    background-color: #fff;
    //flex-shrink: 0;
    //flex-grow: 3;

    .top {
      height: 26px;
      line-height: 26px;
      display: flex;
      justify-content: space-between;
      border-bottom: 1px solid #82848a;
      background-color: #d2d2d2;

      .tool {
        .item {
          display: inline-block;
          font-size: 20px;
          color: #009966;
          margin: 0 20px 0 0;
          line-height: 26px;
          cursor: pointer;

          span {
            color: #000;
            font-size: 16px;
          }

          &:hover {
            background-color: #d77f00;
          }
        }
      }

      .quick {
        //background-color: #ea5959;
        display: flex;

        .tag {
          //display: inline-block;
          //height: 30px;
          //border: 1px solid #FF9933;
          background-color: #ff9900;
          color: #fff;
          border-radius: 3px;
          margin: 2px;
          line-height: 22px;
          padding: 0 5px;
          cursor: pointer;

          &:hover {
            font-weight: 700;
            background-color: #d77f00;
          }
        }

        .iconfont {
          font-size: 20px;
          margin: 0 5px;
          padding: 0 5px;

          &:hover {
            background-color: #d77f00;
            color: #fff;
          }
        }
      }
    }

    .code {
      display: flex;
      //width: 100%;

      height: 400px;
      padding: 0px;
      overflow: auto;
      //background-color: #f13838;
      .multi-sql {
        width: calc(100% - 300px);
        position: relative;

        .tabs {
          //height: 18px;
          //z-index: 1000;
          background-color: #fff;
          position: absolute;
          bottom: 1px;
          left: 30px;
          display: flex;
          z-index:999;

          .tab-active {
            font-weight: 700;
            background-color: rgba(4, 103, 10, 0.18);
          }

          .tab {
            z-index: 1000;
            position: relative;
            cursor: pointer;
            border-top-left-radius: 5px;
            border-top-right-radius: 5px;
            border: 1px solid #ccc;
            border-bottom-width: 0;
            //color: #fff;
            //background-color: #03830a;
            margin: 0 3px;
            width: 80px;
            height: 22px;
            line-height: 22px;
            //padding: 0 3px;
            .text {
              padding: 0 5px;
              text-align: center;
              //width: 50px;
            }

            .close {
              position: absolute;
              right: -10px;
              top: -10px;
              width: 20px;
              display: none;
              background-color: rgba(4, 103, 10, 0.27);
              padding: 3px;
              border-radius: 50%;

              &:hover {
                font-weight: 900;
              }
            }

            &:hover {
              background-color: rgba(4, 103, 10, 0.11);

              .close {
                background-color: rgba(23, 19, 19, 0.17);
                display: block;
              }
            }
          }
        }
      }

      .params {
        padding: 5px;
        width: 600px;
        border-left: 1px solid #82848a;
        //background-color: #ccc;
        display: none;
        overflow: auto;

        :deep(.el-textarea__inner) {
          font-family: "Consolas", Helvetica, Arial, sans-serif !important;
          font-size: 18px !important;
          line-height: 20px;
        }
      }
    }

    .result {
      height: 300px;
      //background-color: #eee;
      display: none;
      border-top: 1px solid #82848a;
      padding: 5px;
      overflow: auto;
      width: 100%;

      .table {
        margin: 5px;
        overflow: auto;
        width: 98%;
      }

      .error {
        color: #f60303;
      }

      .sqlMeta {
        .sql {
          line-height: 20px;
          background-color: #f3f3f3;
          padding: 5px;
          margin: 3px;
          white-space: pre-wrap;
        }
      }
    }
  }
}
</style>