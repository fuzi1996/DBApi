<template>
  <div class='w-screen h-screen bg-gray-800'>
    <div
      class='layout-login'
      @keyup='enterSubmit'
    >
      <h3 class='text-2xl font-semibold text-gray-100 text-center mb-6'>{{ $t('m.system_login') }}</h3>
      <el-form
        ref='ruleForm'
        label-position='right'
        label-width='80px'
        :model='form'
        :rules='rules'
      >
        <el-form-item
          class='mb-6 -ml-20'
          prop='name'
        >
          <el-input
            v-model='form.name'
            :placeholder='$t("m.enter_username")'
            prefix-icon='el-icon-user'
          />
        </el-form-item>
        <el-form-item
          class='mb-6 -ml-20'
          prop='pwd'
        >
          <el-input
            v-model='form.pwd'
            :placeholder='$t("m.enter_password")'
            prefix-icon='el-icon-lock'
            show-password
          />
        </el-form-item>
        <el-form-item class='mb-6 -ml-20'>
          <el-button
            type='primary'
            class='w-full'
            @click='onSubmit'
          >{{ $t('m.login') }}</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, reactive, ref } from 'vue'
import { useLayoutStore } from '/@/store/modules/layout'
import { ElNotification } from 'element-plus'
import { validate } from '/@/utils/formExtend'
import { useI18n } from 'vue-i18n'
import { getMode } from '/@/api/api/api'

const formRender = () => {
  const { login,setMode } = useLayoutStore()
  const { t } = useI18n()
  let form = reactive({
    name: 'admin',
    pwd: 'admin'
  })
  const ruleForm = ref(null)
  const enterSubmit = (e:KeyboardEvent) => {
    if(e.key === 'Enter') {
      onSubmit()
    }
  }
  const onSubmit = async() => {
    let { name, pwd } = form
    if(!await validate(ruleForm)) return
    const message = await login({ username: name, password: pwd })
    if(message) {
      ElNotification({
        title: t('m.error'),
        message,
        type: 'warning'
      })
    }else{
      getMode().then(mode => {
        setMode(mode)
      })
      ElNotification({
        title:  t('m.welcome'),
        message:  t('m.welcome_back'),
        type: 'success'
      })
    }
    
  }
  const rules = reactive({
    name: [
      { validator: (rule: any, value: any, callback: (arg0?: Error|undefined) => void) => {
        if (!value) {
          return callback(new Error(t('m.user_name_cant_empty')))
        }
        callback()
      }, trigger: 'blur' 
      }
    ],
    pwd: [
      { validator: (rule: any, value: any, callback: (arg0?: Error|undefined) => void) => {
        if (!value) {
          return callback(new Error(t('m.passwd_cant_empty')))
        }
        callback()
      }, trigger: 'blur' 
      }
    ]
  })
  return {
    form, 
    onSubmit,
    enterSubmit,
    rules,
    ruleForm
  }
}
export default defineComponent({
  name: 'Login',
  setup() {
    return {
      labelCol: { span: 4 },
      wrapperCol: { span: 14 },
      ...formRender()
    }
  }
})
</script>

<style lang='postcss' scoped>
.layout-login {
    padding-top: 200px;
    width: 400px;
    margin: 0 auto;

    ::v-deep(.el-input__inner) {
        border: 1px solid hsla(0, 0%, 100%, 0.1);
        background: rgba(0, 0, 0, 0.1);
        border-radius: 5px;
        color: #000;
    }
}

</style>