<template>
  <div class='sqlcode-codemirror-container'>
    <codemirror
      ref='codemirror'
      v-model='sqlText'
      :autofocus='isAutoFocus'
      :indent-with-tab='indentWithTab'
      :tab-size='tabSize'
      :extensions='extensions'
      :style='codeMirrorStyle'
      @ready='onCodeMirrorInstantReady'
      @focus='handleCodeMirrorFocus'
    />
  </div>
  
</template>
<script lang="ts">
import { defineComponent,ref,shallowRef } from 'vue'
import { sql } from '@codemirror/lang-sql'
import type { PropType } from 'vue'
import { ViewUpdate } from '@codemirror/view'
import { EditorState } from '@codemirror/state'
import { EditorView } from '@codemirror/view'

export default defineComponent({
  props:{
    modelValue:{
      type:String,
      default: ''
    },
    isAutoFocus:{
      type:Boolean,
      default: true
    },
    indentWithTab:{
      type:Boolean,
      default: true
    },
    tabSize:{
      type:Number,
      default: 2
    },
    codeMirrorStyle: {
      type: Object as PropType<Record<string, unknown>>,
      default() {
        return {
          height: '100%',
          width: '100%'
        }
      }
    }
  },
  emits:['update:modelValue'],
  setup(props,ctx) {
    const sqlText = ref<string>(props.modelValue)
    const extensions = [sql()]
    let codemirror = null
    let currentView:EditorView|any = undefined

    const handleCodeMirrorFocus = () => {
      ctx.emit('update:modelValue',sqlText)
    }
    const onCodeMirrorInstantReady = ({ view }:any) => {
      currentView = view
    }
    const getSelection = ():string => {
      if(currentView) {
        return currentView.state.sliceDoc(
          currentView.state.selection.main.from,
          currentView.state.selection.main.to
        )
      }
      console.warn('不存在 codemirror view 无法获取选中的文本')
      return ''
    }
    const setValue = (sql:string) => {
      sqlText.value = sql
    }
    const getValue = () => {
      return sqlText.value
    }
    return {
      sqlText,
      extensions,
      codemirror,
      setValue,
      getValue,
      getSelection,
      handleCodeMirrorFocus,
      onCodeMirrorInstantReady
    }
  }
})
</script>
<style scoped>
.sqlcode-codemirror-container{
  height: 100%;
  width: 100%;
}
</style>
