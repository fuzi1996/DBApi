<template>
  <div>
    <table-search
      :current-page='tableData.page'
      :page-size='tableData.size'
      :total='tableData.total'
      class='m-3'
      @size-change='handleSizeChange'
      @current-change='handleCurrentChange'
    >
      <template #button-group>
        <el-button-group>
          <el-button
            type='primary'
            @click='handleAddApiBtnClick'
          >
            新增API
          </el-button>
          <el-button
            type='primary'
            @click='handleApiGroupVisiableBtnClick'
          >
            API分组管理
          </el-button>
        </el-button-group>
      </template>
      <template #search>
        <el-row
          :gutter='15'
          class='clear-both'
        >
          <el-col :span='24'>
            <card-list
              title='查询'
              type='keyvalue'
              :show-header='true'
            >
              <template #btn>
                <el-button-group>
                  <el-button
                    icon='el-icon-search'
                    size='small'
                    @click='tableQuery'
                  >搜索</el-button>
                </el-button-group>
              </template>
              <template #keyvalue>
                <el-form
                  ref='refForm'
                  class='card-list-form'
                  :model='form'
                  size='small'
                >
                  <el-row :gutter='15'>
                    <card-list-item
                      width='100px'
                      prop='groupId'
                    >
                      <template #key>API分组</template>
                      <template #value>
                        <el-select
                          v-model='form.groupId'
                          filterable
                        >
                          <el-option
                            v-for='item in dynamicTags'
                            :key='item.id'
                            :label='item.name'
                            :value='item.id'
                          />
                        </el-select>
                      </template>
                    </card-list-item>
                    <card-list-item
                      width='100px'
                      prop='keyword'
                      :has-key='false'
                    >
                      <template #value>
                        <el-input
                          v-model='form.keyword'
                          placeholder='Please input'
                          class='input-with-select'
                        >
                          <template #prepend>
                            <el-select
                              v-model='form.field'
                              placeholder='Select'
                              style='width: 85px'
                            >
                              <el-option
                                v-for='item in fields'
                                :key='item.value'
                                :label='item.label'
                                :value='item.value'
                              />
                            </el-select>
                          </template>
                        </el-input>
                      </template>
                    </card-list-item>
                  </el-row>
                </el-form>
              </template>
            </card-list>
          </el-col>
        </el-row>
      </template>
      <el-table
        :data='tableData.data'
        border
        stripe
        max-height='700'
        width='100%'
      >
        <el-table-column
          label='id'
          prop='id'
          width='100px'
        />
        <el-table-column :label='$t("m.name")'>
          <template #default='scope'>
            <i
              v-if='scope.row.status == 1'
              class='iconfont icon-on_line1 circle'
            />
            <i
              v-else
              class='iconfont icon-off_line circle offline'
            />
            <i
              v-if='scope.row.previlege == PREVILEGE.PRIVATE'
              class='el-icon-lock circle lock'
            />
            <i
              v-else
              class='el-icon-unlock circle '
            />
            <span :title='scope.row.note'>{{ scope.row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column :label='$t("m.path")'>
          <template #default='scope'>
            <span>/{{ context }}/{{ scope.row.path }}</span>
          </template>
        </el-table-column>
        <el-table-column
          label='事务'
          width='60'
        >
          <template #default='scope'>
            <span v-if='scope.row.openTrans == 1'>开启</span>
            <span v-if='scope.row.openTrans == 0'>关闭</span>
          </template>
        </el-table-column>
        <el-table-column
          label='Content-Type'
          prop='contentType'
          sortable
        />
        <el-table-column :label='$t("m.parameters")'>
          <template #default='scope'>
            <div v-show='scope.row.contentType === CONTENT_TYPE.FORM_URLENCODED '>
            <!-- <data-tag
            v-for='item in scope.row.p'
            :name='item.name'
            :type='item.type'
          /> -->
            </div>
            <div v-show='scope.row.contentType === CONTENT_TYPE.JSON '>
              {{ scope.row.jsonParam }}
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop='updateTime'
          :label='$t("m.update_time")'
          width='170px'
          sortable
        />
        <el-table-column
          :label='$t("m.operation")'
          width='220px'
        >
          <template #default='scope'>
            <el-button
              plain
              size='small'
              type='info'
              circle
              @click='detail(scope.row.id)'
            ><i class='iconfont icon-detail' />
            </el-button>
            <el-button
              plain
              size='small'
              type='warning'
              circle
              @click='handleEdit(scope.row.id)'
            >
              <el-icon>
                <Edit />
              </el-icon>
            </el-button>

            <el-button
              v-if='scope.row.status == 0'
              plain
              size='small'
              type='warning'
              circle
              @click='online(scope.row.id)'
            >
              <i class='iconfont icon-on_line2' />
            </el-button>

            <el-button
              v-if='scope.row.status == 1'
              plain
              size='small'
              type='info'
              circle
              @click='offline(scope.row.id)'
            >
              <i class='iconfont icon-off_line1' />
            </el-button>

            <el-button
              v-if='scope.row.status == 1'
              plain
              size='small'
              type='primary'
              :title='$t("m.request_test")'
              circle
              @click='httpTest(scope.row.id)'
            >
              <i class='iconfont icon-HTTPRequest' />
            </el-button>
            <el-button
              plain
              size='small'
              type='danger'
              circle
              @click='handleDelete(scope.row.id)'
            >
              <el-icon>
                <Delete />
              </el-icon>
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </table-search>

    <open-window
      v-model:is-show='apiGroupVisiable'
      title='API分组管理'
    >
      <ApiGroup />
    </open-window>
  </div>
</template>

<script lang="ts">
import { defineComponent, reactive, ref,onMounted } from 'vue'
import CardList from '/@/components/CardList/CardList.vue'
import CardListItem from '/@/components/CardList/CardListItem.vue'
import TableSearch from '/@/components/TableSearch/index.vue'
import { CONTENT_TYPE,PREVILEGE } from '/@/constant/common'
import { getAllApis,IApiConfigItem,getContext,deleteApi,offlineApi,onlineApi } from '/@/api/api/api'
import OpenWindow from '/@/components/OpenWindow/index.vue'
import ApiGroup from './apiGroup.vue'
import { listAllGroup,IGroupItem,createGroup,deleteGroup } from '/@/api/api/group'
import router from '/@/router/index'
import { ElMessage } from 'element-plus'

interface ISearchForm {
  keyword: string
  field: string
  groupId: string
}

interface IFieldItem {
  label: string
  value: string
}

export default defineComponent({
  name: 'ApiList',
  components: {
    TableSearch,
    CardList,
    CardListItem,
    OpenWindow,
    ApiGroup
  },
  setup() {

    // const 
    const tableData: ITable<IApiConfigItem> = reactive({
      data : [],
      total: 0,
      page: 1,
      size: 10
    })

    const form: ISearchForm = reactive({
      keyword: '',
      field: 'name',
      groupId: ''
    })

    const apiGroupVisiable = ref<boolean>(false)

    const context = ref<string>('')

    const dynamicTags = ref<IGroupItem[]>([])

    const filterDynamicTags = ref<IGroupItem[]>([])

    const queryApiGroupSearch = (queryString: string) => {
      const apiGroupList = dynamicTags.value
      const results = apiGroupList.filter(item => 
        item.name.toLowerCase().indexOf(queryString.toLowerCase()) === 0
      )
      filterDynamicTags.value.splice(0)
      filterDynamicTags.value.push(...results)
    }

    const loadAllGroup = () => {
      listAllGroup().then(({ data }) => {
        dynamicTags.value.splice(0)
        dynamicTags.value.push(...data.data)
      })
    }

    const handleApiGroupVisiableBtnClick = () => {
      apiGroupVisiable.value = true
    }

    onMounted(() => {
      loadAllGroup()
      getContext().then(str => {
        context.value = str
      })
      query()
    })

    const query = () => {
      getAllApis().then(({ data }) => {
        tableData.data.splice(0)
        tableData.data.push(...data.data)
      })
    }

    const tableQuery = () => {
      query()
    }

    const detail = (id:string):void => {
      router.push({ name: 'ApiEdit',query:{ id,readOnly:'true' } })
    }

    const handleEdit = (id:string):void => {
      router.push({ name: 'ApiEdit',query:{ id } })
    }
    const online = (id:string):void => {
      onlineApi(id).then(data => {
        apiOperationCallback(data,'已上线')
        query()
      })
    }
    const offline = (id:string):void => {
      offlineApi(id).then(data => {
        apiOperationCallback(data,'已下线')
        query()
      })
    }
    const apiOperationCallback = (data:IResponse<any>,msg:string) => {
      if(!data.success) {
        ElMessage.error(data.msg)
      }else{
        ElMessage.success(msg)
      }
    }
    const httpTest = (id:string):void => {
      router.push({ name: 'ApiReqeust', query: { id } })
    }
    const handleDelete = (id:string):void => {
      deleteApi(id).then(data => {
        apiOperationCallback(data,'已删除')
        query()
      })
    }

    const handleAddApiBtnClick = () => {
      router.push({ name: 'ApiEdit' })
    }

    const handleSizeChange = (v: number) => (tableData.size = v) && query()
    const handleCurrentChange = (v: number) => (tableData.page = v) && query()
    const fields:IFieldItem[] = [
      { label: '描述',value:'note' },
      { label: '路径',value:'path' },
      { label: '名称',value:'name' }
    ]
    return {
      fields:Object.freeze(fields),
      CONTENT_TYPE:Object.freeze(CONTENT_TYPE),
      PREVILEGE:Object.freeze(PREVILEGE),
      context,
      form,
      tableData,
      apiGroupVisiable,
      dynamicTags,
      query,
      detail,
      handleEdit,
      online,
      offline,
      httpTest,
      handleDelete,
      handleCurrentChange,
      handleSizeChange,
      handleApiGroupVisiableBtnClick,
      queryApiGroupSearch,
      tableQuery,
      handleAddApiBtnClick
    }
  }
})
</script>
<style scoped>
.input-with-select{
  width: 100%;
}
</style>