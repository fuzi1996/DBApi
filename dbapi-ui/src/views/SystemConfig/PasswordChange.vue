<template>
  <el-form
    :model='form'
    label-width='120px'
  >
    <el-form-item label='密码'>
      <el-input
        v-model='form.password'
        class='passwd-input'
      />
    </el-form-item>
    <el-form-item label='确认密码'>
      <el-input
        v-model='form.repassword'
        class='passwd-input'
      />
    </el-form-item>
    <el-form-item>
      <el-button
        type='primary'
        @click='onSubmit'
      >提交</el-button>
    </el-form-item>
  </el-form>
</template>

<script lang="ts" setup name="ChangePassword">
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { changePassword,changePasswordParam } from '/@/api/user'
import { useLayoutStore } from '/@/store/modules/layout'

const form = reactive({
  password: '',
  repassword: ''
})

const onSubmit = async() => {
  const { logout } = useLayoutStore()
  if (form.password !== form.repassword) {
    ElMessage.error('两次输入的密码不一样！')
    return
  }
  if (form.password == '' || form.password == null) {
    ElMessage.warning('请输入密码！')
    return
  }

  const msg = await resetPassword({ password: form.password })
  if(msg === '') {
    ElMessage.success('重置成功')
    logout()
  }else{
    ElMessage.error(msg)
  }
}

const resetPassword = async(param: changePasswordParam):Promise<string> => {
  const res = await changePassword(param)
  return res.data.msg
}
</script>
<style scoped>
.passwd-input{
  width: 220px;
}
</style>
