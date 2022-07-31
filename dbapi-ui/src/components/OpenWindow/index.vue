<template>
  <transition name='el-fade-in'>
    <el-dialog
      :model-value='isShow' 
      :title='title'
      :width='width'
      :before-close='handleBeforeClose'
    >
      <slot name='default' />
      <template #footer>
        <span class='dialog-footer'>
          <slot name='btn' />
          <el-button @click='close'>{{ closeBtnLabel }}</el-button>
        </span>
      </template>
    </el-dialog>
  </transition>
</template>

<script lang="ts">
import { defineComponent, SetupContext,ref } from 'vue'
export default defineComponent({
  name: 'SelectPage',
  props: {
    isShow: {
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: '新窗口'
    },
    width:{
      type: String,
      default: '60%'
    },
    closeBtnLabel:{
      type: String,
      default: '关闭'
    }
  },
  emits: ['update:show','beforeClose'],
  setup(props, context: SetupContext) {
    const close = () => {
      context.emit('update:show', !props.isShow)
    }
    const handleBeforeClose = () => context.emit('beforeClose')
    return {
      close,
      handleBeforeClose,
      slots: context.slots
    }
  }
})
</script>
<style scoped>

</style>