<template>
  <div class='mycontent'>
    <dataSourceCommon
      :id='$route.query.id'
      ref='detail'
      :disabled='$route.query.disabled'
    >
      <template #button>
        <el-button
          type='primary'
          plain
          @click='save'
        >
          {{ $t('m.save') }}
        </el-button>  
      </template>
    </dataSourceCommon>
    
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import request from '/@/utils/request'
import { useLayoutStore } from '/@/store/modules/layout'
import DataSourceCommon from './dataSourceCommon.vue'
import { updateDataSource,addDataSource,deleteDataSourceById,exportDataSource,IDataSourceMeta } from '/@/api/datasource'
import { ElMessage } from 'element-plus'
export default defineComponent({
  components:{
    DataSourceCommon
  },
  methods: {
    save() {
      const dataSourceInstance = this.$refs.detail as typeof DataSourceCommon
      const data = dataSourceInstance.getData()
      const { removeActiveTag } = useLayoutStore()
      const saveMethod = data.id ? updateDataSource : addDataSource
      saveMethod({
        id: data.id,
        name: data.name,
        note: data.note,
        url: data.url,
        username: data.username,
        password: data.password,
        edit_password: data.edit_password,
        type: data.type,
        driver: data.driver,
        tableSql: data.tableSql
      })
        .then((data) => {
          ElMessage.success('Success')
          this.$router.push('/datasource/list')
          removeActiveTag()
        })
        .catch((error) => {
          console.error('error',error)
          ElMessage.error('Failed')
        })
    }
  }
})
</script>

<style scoped>

</style>
