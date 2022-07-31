<template>
  <div>
    <el-form
      label-width='200px'
      :disabled='isDisabled'
    >
      <el-form-item :label='$t("m.database")'>
        <el-select
          v-model='detail.type'
          placeholder='请选择'
          @change='selectDB'
        >
          <el-option
            v-for='item in options'
            :key='item.label'
            :label='item.label'
            :value='item.value'
          >
            <db-icon :type='item.value' />
            <span>{{ item.label }}</span>
          </el-option>
        </el-select>
        <el-alert
          v-show='detail.type == "others"'
          type='warning'
          show-icon
          :title='$t("m.ds_driver_tip")'
          style='margin-top: 10px;'
        />
      </el-form-item>
      <el-form-item :label='$t("m.name")'>
        <el-input v-model='detail.name' />
      </el-form-item>
      <el-form-item :label='$t("m.note")'>
        <el-input v-model='detail.note' />
      </el-form-item>
      <el-form-item :label='$t("m.jdbc_driver_class")'>
        <el-input v-model='detail.driver' />
      </el-form-item>
      <el-form-item label='Jdbc Url'>
        <el-input
          v-model='detail.url'
          type='textarea'
          class='my'
        />
      </el-form-item>
      <el-form-item :label='$t("m.username")'>
        <el-input v-model='detail.username' />
      </el-form-item>

      <el-form-item
        :label='$t("m.password")'
        prop='password'
      >
        <el-input
          v-model='detail.password'
          prefix-icon='el-icon-lock'
          placeholder='请输入密码'
          style='width: 80%;'
          :type='[flag?"text":"password"]'
          :disabled='!detail.edit_password'
        >
          <i
            slot='suffix'
            :class='[flag?"el-icon-minus":"el-icon-view"]'
            style='margin-top:8px;font-size:18px;'
            autocomplete='auto'
            @click='flag=!flag'
          />
        </el-input>
        <div class='edit-password'>
          {{ $t("m.edit_password") }}: 
          <el-checkbox
            v-model='detail.edit_password'
            @change='checked'
          />
        </div>
      </el-form-item>
      <el-form-item :label='$t("m.sql_query_all_table_name")'>
        <el-input v-model='detail.tableSql' />
        <el-alert
          type='warning'
          :title='$t("m.ds_sql_tip")'
          show-icon
          style='margin-top: 10px;'
        />
      </el-form-item>

      <el-form-item v-if='!isDisabled'>
        <el-button
          type='primary'
          plain
          style='margin: 10px 0;'
          @click='connect'
        >
          {{ $t('m.test_connection') }}
        </el-button>
        <slot name='button' />
      </el-form-item>
    </el-form>
  </div>
</template>

<script lang="ts">
import request from '/@/utils/request'
import { defineComponent } from 'vue'
import { DATASOURCE_META_DATA,DB_OPTIONS } from '/@/constant/dataSource'
import { GLOBAL_FRONTENT_URL_PREFIX } from '/@/constant'
import { connectDataSource,loadDataSourceDetail,deleteDataSourceById,exportDataSource,IDataSourceMeta } from '/@/api/datasource'
import { ElMessage } from 'element-plus'
export default defineComponent({
  name: 'Common',
  props: {
    id:{
      type:String,
      default: ''
    },
    // 参数在路由中被转为字符串
    disabled:{
      type:String,
      default: 'true'
    }
  },
  data() {
    const detail:IDataSourceMeta = {
      id:'',
      url: '',
      name: '',
      note: '',
      type: '',
      username: '',
      password: '',
      edit_password: false,
      driver: '',
      tableSql: ''
    }
    return {
      flag: false,
      options: Object.freeze(DB_OPTIONS),
      detail,
      ds: Object.freeze(DATASOURCE_META_DATA)
    }
  },
  computed:{
    isDisabled() {
      return this.disabled === 'true'
    }
  },
  created() {
    if (this.id) {
      this.getDetail(this.id)
    }
  },
  methods: {
    getData() {
      return this.detail
    },
    checked() {
      if (this.detail.edit_password) {
        this.detail.password = ''
      }
    },
    selectDB() {
      this.detail.url = (this.ds[this.detail.type]).url
      this.detail.driver = (this.ds[this.detail.type]).driver
      this.detail.tableSql = (this.ds[this.detail.type]).sql
    },
    getDetail(id:string) {
      loadDataSourceDetail(id)
        .then((data) => {
          this.detail = data.data
        })
        .catch((error) => {
          console.error('error',error)
          ElMessage.error('failed')
        })
    },
    connect() {
      connectDataSource({
        url: this.detail.url,
        username: this.detail.username,
        password: this.detail.password,
        edit_password: this.detail.edit_password,
        driver: this.detail.driver
      })
        .then(({ data }) => {
          console.log('data',data)
          if (data.success)
            ElMessage.success('Success')
          else
            ElMessage.error(data.msg)
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



.edit-password{
  margin-left: 20px;
}

</style>
