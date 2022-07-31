<template>
  <div>
    <div>
      <ul>
        <li>
          <el-button
            type='primary'
            icon='el-icon-plus'
            @click='handleAdd'
          >{{ $t('m.create_ds') }}</el-button>
        </li>
        <li>
          <el-button
            type='warning'
            icon='el-icon-download'
            round
            @click='exportDataSource'
          >{{ $t('m.export_ds') }}</el-button>
        </li>
        <li>
          <el-upload
            :action='DATA_SOURCE_IMPORT_URL'
            accept='.json'
            :on-success='importSuccess'
            :headers='headers'
            :on-error='importFail'
            :file-list='fileList'
          >
            <el-button
              type='warning'
              icon='Upload'
              round
            >{{ $t('m.import_ds') }}</el-button>
          </el-upload>
        </li>
      </ul>
    </div>

    <el-table
      :data='tableData'
      border
      stripe
      max-height='700'
      @selection-change='handleSelectionChange'
    >
      <el-table-column
        type='selection'
        width='55'
      />
      <el-table-column
        prop='id'
        label='id'
        width='270px'
      />
      <el-table-column :label='$t("m.name")'>
        <template #default='scope'>
          <db-icon :type='scope.row.type' />
          <span>{{ scope.row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop='note'
        :label='$t("m.note")'
      />
      <el-table-column
        prop='updateTime'
        :label='$t("m.update_time")'
        width='170px'
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
          >
            <i class='iconfont icon-detail' />
          </el-button>
          <el-button
            plain
            size='small'
            type='warning'
            circle
            @click='handleEdit(scope.row.id)'
          >
            <el-icon><Edit /></el-icon>
          </el-button>
          <el-button
            plain
            size='small'
            type='danger'
            circle
            @click='handleDelete(scope.row.id)'
          >
            <el-icon> <Delete /></el-icon>
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script lang="ts">
import request from '/@/utils/request'
import { defineComponent } from 'vue'
import { ElMessage,ElLoading, ElNotification } from 'element-plus'
import { useLayoutStore } from '/@/store/modules/layout'
import { DATA_SOURCE_IMPORT_URL,listAllDataSource,deleteDataSourceById,exportDataSource,IDataSourceMeta } from '/@/api/datasource'

export default defineComponent({
  name: 'Datasource',
  data() {
    const { getToken } = useLayoutStore()
    const tableData:IDataSourceMeta[] = []
    const selectRows:IDataSourceMeta[] = []
    return {
      tableData,
      show: false,
      headers: {
        Authorization: getToken()
      },
      fileList: [],
      selectRows,
      DATA_SOURCE_IMPORT_URL:Object.freeze(DATA_SOURCE_IMPORT_URL)
    }
  },
  created() {
    this.getAllSource()
  },
  methods: {
    handleSelectionChange(val:IDataSourceMeta[]) {
      this.selectRows.splice(0)
      this.selectRows.push(...val)
    },
    exportDataSource() {
      const idList = this.selectRows.map(t => t.id)
      const ids = idList.join(',')
      exportDataSource(ids).then((res) => {
        const link = document.createElement('a')
        let blob = new Blob([res.data], { type: 'application/x-msdownload' })
        link.style.display = 'none'
        link.href = URL.createObjectURL(blob)
        link.setAttribute('download', 'datasource.json')
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
      })
        .catch(error => {
          ElMessage.error('Export Failed')
          console.error(error)
        })
    },
    importSuccess(response:any, file:any, fileList:any) {
      this.fileList = []
      ElMessage.success('Import success')
      this.getAllSource()
    },
    importFail(error:any, file:any, fileList:any) {
      ElMessage.error(`Import failed!  ${error.message}`)
    },
    detail(id:string) {
      this.$router.push({ path: '/datasource/edit', query: { id: id } })
    },
    handleAdd() {
      this.$router.push({ path: '/datasource/edit', query: { disabled: 'false' } })
    },
    handleEdit(id:string) {
      this.$router.push({ path: '/datasource/edit', query: { id: id, disabled: 'false' } })
    },
    getAllSource() {
      listAllDataSource().then(data => {
        this.tableData.length = 0
        this.tableData.push(...data)
      })
        .catch(error => {
          console.error('error',error)
          ElMessage.error('Get All DataSource Error')
        })
    },
    handleDelete(id:string) {
      deleteDataSourceById(id).then((data) => {
        if (data.success) {
          ElMessage.success('Delete Success')
        } else {
          ElMessage.error(data.msg)
        }
        this.getAllSource()
      })
        .catch((error) => {
          ElMessage.error('Delete Failed')
        })
    }
  }
})
</script>

<style scoped>
ul {
  margin-bottom: 10px;
}

ul > li {
  display: inline-block;
  margin-right: 10px;
}
</style>
