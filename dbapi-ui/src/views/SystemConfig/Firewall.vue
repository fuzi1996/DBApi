<template>
  <div>
    <el-form label-width='200px'>
      <el-form-item :label='$t("m.status")'>
        <el-switch
          v-model='status'
          active-color='#13ce66'
          active-value='on'
          inactive-value='off'
          :active-text='$t("m.on")'
          :inactive-text='$t("m.off")'
        />
      </el-form-item>
      <div v-show='status=="on"'>
        <el-form-item :label='$t("m.mode")'>
          <el-radio-group v-model='mode'>
            <el-radio label='black'>{{ $t('m.black_list') }}</el-radio>
            <el-radio label='white'>{{ $t('m.white_list') }}</el-radio>
          </el-radio-group>
          <el-alert
            v-show='mode == "black"'
            :title='$t("m.black_tip")'
            type='warning'
            :closable='false'
          />
          <el-alert
            v-show='mode == "white"'
            :title='$t("m.white_tip")'
            type='warning'
            :closable='false'
          />
        </el-form-item>
        <el-form-item :label='$t("m.ip_list")'>
          <el-input
            v-model='ips'
            type='textarea'
            :autosize='{ minRows: 8, maxRows: 20 }'
            :placeholder='$t("m.ip_list_tip")'
          />
        </el-form-item>
      </div>
      <el-form-item>
        <el-button type='primary' plain @click='submit'>{{ $t('m.save') }}</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script lang="ts" setup>
import { onMounted,ref } from 'vue'
import { ElMessage } from 'element-plus'
import { loadFirewallDetail,saveFirewallDetail } from '/@/api/firewall'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const status = ref('on')
const mode = ref('black')
const ips = ref('')

const getFirewallDetail = () => {
  loadFirewallDetail().then(res => {
    const { data } = res.data
    status.value = data.status
    mode.value = data.mode
    ips.value = data.ips
  })
}

onMounted(getFirewallDetail)

const submit = () => {
  saveFirewallDetail({ status: status.value, mode: mode.value, ips: ips.value }).then(() => {
    ElMessage.success(t('m.save_success'))
    getFirewallDetail()
  })
}

</script>

<style scoped>

</style>