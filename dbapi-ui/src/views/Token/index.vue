<template>
  <div>
    <div class='gap'>
      <el-button type='primary' plain @click='dialogCreateApp = true'>创建应用</el-button>
    </div>

    <el-table :data='tableData' border stripe max-height='700' class='gap'>
      <el-table-column prop='id' label='appid' />
      <el-table-column prop='name' label='应用名称' />
      <el-table-column prop='note' label='描述' />
      <el-table-column prop='secret' label='secret' />
      <el-table-column prop='expireDesc' label='token失效时间' />
      <el-table-column prop='note' :label='$t("m.note")' />
      <el-table-column :label='$t("m.operation")' width='100px'>
        <template #default='scope'>
          <el-button plain size='small' type='warning' circle @click='handleAuth(scope.row.id)'>
            <el-icon><Lock /></el-icon>
          </el-button>
          <el-button plain size='small' type='danger' circle @click='handleDelete(scope.row.id)'>
            <el-icon><Delete /></el-icon>
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model='dialogVisible' title='授权该app访问以下分组的API' @open='getAllGroups'>
      <el-checkbox-group v-model='checkList'>
        <el-checkbox v-for='item in groups' :key='item.id' :label='item.id'>{{ item.name }}</el-checkbox>
      </el-checkbox-group>

      <span slot='footer' class='dialog-footer'>
        <el-button @click='dialogVisible = false'>{{ $t('m.cancel') }}</el-button>
        <el-button type='primary' @click='dialogVisible = false;auth()'>{{ $t('m.save') }}</el-button>
      </span>
    </el-dialog>


    <el-alert type='warning' show-icon>
      <div class='tip' />
      <div><br>
        import requests <br>
        headers = {"Authorization": "5ad0dcb4eb03d3b0b7e4b82ae0ba433f"} <br>
        re = requests.post("http://127.0.0.1:8520/api/userById", {"idList": [1, 2]}, headers=headers) <br>
        print(re.text) <br>
      </div>

    </el-alert>

    <el-dialog v-model='dialogCreateApp' title='创建应用' width='40%'>
      <el-form label-width='120px'>
        <el-form-item label='应用名称'>
          <el-input v-model='app.name' />
        </el-form-item>
        <el-form-item label='描述'>
          <el-input v-model='app.note' type='textarea' />
        </el-form-item>
        <el-form-item label='token过期时间'>
          <el-radio-group v-model='app.expireDesc'>
            <el-radio-button label='5min'>5min</el-radio-button>
            <el-radio-button label='1hour'>1hour</el-radio-button>
            <el-radio-button label='1day'>1day</el-radio-button>
            <el-radio-button label='30day'>30day</el-radio-button>
            <el-radio-button label='单次有效'>单次有效</el-radio-button>
            <el-radio-button label='永久有效'>永久有效</el-radio-button>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot='footer' class='dialog-footer'>
        <el-button @click='dialogCreateApp = false;resetApp()'>取 消</el-button>
        <el-button type='primary' @click='dialogCreateApp = false;saveApp();'>确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { onMounted,ref,reactive } from 'vue'
import { ElMessage,ElLoading, ElNotification } from 'element-plus'
import { getAllApp,createApp,deleteApp,loadAuthGroups,authApp,ICreateAppParam,IAppItem } from '/@/api/app'
import { listAllGroup,IGroupItem } from '/@/api/api/group'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const tableData = reactive<IAppItem[]>([])
const dialogVisible = ref<boolean>(false)
const dialogCreateApp = ref<boolean>(false)
const groups = reactive<IGroupItem[]>([])
const checkList = ref<string[]>([])
const tokenId = ref<string>('')
const app = reactive<ICreateAppParam>({
  name: '',
  note: '',
  expireDesc: ''
})
const resetApp = () => {
  app.name = ''
  app.note = ''
  app.expireDesc = ''
}
const saveApp = () => {
  createApp(app).then(response => {
    const { data } = response.data
    const msg = `创建应用成功！请妥善保存:<br/><br/>appid=${data.id}<br/>secret=<br/>${data.secret}`
    console.log('msg',msg)
    ElNotification({
      title: '注意',
      dangerouslyUseHTMLString: true,
      message: msg
    })
    listApp()
    resetApp()
  })
}
const listApp = () => {
  getAllApp().then((response) => {
    const { data } = response.data
    tableData.length = 0
    tableData.push(...data)
  })
    .catch((error) => {
      console.log('error',error)
    })
}
onMounted(listApp)
const getAllGroups = () => {
  groups.length = 0
  listAllGroup().then((res) => {
    const { data } = res.data
    if(Array.isArray(data)) {
      console.log('groups data',data)
      groups.push(...data)
    }
  })
}
const getAuthGroups = (id:string) => {
  checkList.value.length = 0
  loadAuthGroups(id).then(res => {
    const { data } = res.data
    checkList.value.push(...data)
  })
}
const auth = () => {
  console.log(checkList)
  authApp({ appId: tokenId.value,groupIds:checkList.value }).then((response) => {
    ElMessage.success('Authorization Success')
  })
    .catch((error) => {
      ElMessage.error('Authorization Failed')
    })
}
const handleDelete = (id:string) => {
  deleteApp(id).then(() => {
    ElMessage.success('Delete Success')
    listApp()
  })
    .catch(() => {
      ElMessage.error('Delete Failed')
    })
}
const handleAuth = (id:string) => {
  dialogVisible.value = true
  tokenId.value = id
  getAuthGroups(id)
}
</script>

<style scoped>
.tip {
  white-space: pre;
}
</style>