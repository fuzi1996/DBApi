<template>
  <div
    ref='codeViewContainerRef'
    class='code-view-container'
  >
    <h1
      ref='displayNameRef'
      :class='{&apos;display-name-unfirst&apos;:!isFirst,&apos;display-name-first&apos;:isFirst}'
    >{{ langDisplayName }}</h1>
    <el-card
      @mouseover='onMouseOverCard'
      @mouseleave='onMouseLeaveCard'
    >
      <div class='sqlcode-codemirror-container'>
        <codemirror
          ref='codemirror'
          v-model='codeValue'
          :autofocus='isAutoFocus'
          :indent-with-tab='indentWithTab'
          :tab-size='tabSize'
          :extensions='extensions'
          :style='codeMirrorStyle'
          class='code-block'
          @ready='onCodeMirrorInstantReady'
          @focus='handleCodeMirrorFocus'
          @mouseenter='onMouseEnterCodemirror'
        />
      </div>
    </el-card>
    <el-button
      v-show='btnVisiable'
      ref='btnRef'
      class='code-btn'
      :style='btnStyle'
      plain
      size='small'
      :icon='iconName'
      @click='copyCode'
      @mouseover='onMouseOverBtn'
    />
  </div>
  
  
</template>
<script lang="ts">
import { defineComponent,ref,reactive,watch } from 'vue'
import { sql } from '@codemirror/lang-sql'
import { java } from '@codemirror/lang-java'
import { javascript } from '@codemirror/lang-javascript'
import { python } from '@codemirror/lang-python'
import type { PropType } from 'vue'
import { ViewUpdate } from '@codemirror/view'
import { EditorState } from '@codemirror/state'
import { EditorView } from '@codemirror/view'
import useClipboard from 'vue-clipboard3'
import { LANGUAGE } from '/@/constant'

interface ICONDefine {
  COPY: string
  CHECK: string
}

const ICON:ICONDefine = {
  COPY: 'el-icon-document-copy',
  CHECK: 'el-icon-check'
}

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
    langDisplayName: {
      type: String,
      default: ''
    },
    width: {
      type: String,
      default: '100%'
    },
    isFirst: {
      type: Boolean,
      defautl: false
    },
    height: {
      type: String,
      default: '100%'
    },
    codeMirrorStyle: {
      type: Object as PropType<Record<string, unknown>>,
      default() {
        return {
          height: '100%',
          width: '100%'
        }
      }
    },
    mode:{
      type: String,
      default: ''
    }
  },
  emits:['update:modelValue'],
  setup(props,ctx) {
    const { toClipboard } = useClipboard()
    const codeValue = ref<string>(props.modelValue)

    watch(() => props.modelValue,(val) => {
      codeValue.value = val
    })

    const iconName = ref<string>(ICON.COPY)
    const btnVisiable = ref<boolean>(false)
    
    const codeViewContainerRef = ref<HTMLInputElement | null>(null)
    const displayNameRef = ref<HTMLInputElement | null>(null)
    const btnRef = ref<any>(null)
    const btnStyle = reactive<any>({
      top: '',
      left: ''
    })
    let codemirror = null
    let currentView:EditorView|any = undefined

    let extensions:any[] = []
    if(props.mode === LANGUAGE.JAVA) {
      extensions = [java()]
    }else if(props.mode === LANGUAGE.JAVASCRIPT) {
      extensions = [javascript()]
    }else if(props.mode === LANGUAGE.PYTHON) {
      extensions = [python()]
    }else{
      extensions = [javascript()]
    }

    const handleCodeMirrorFocus = () => {
      ctx.emit('update:modelValue',codeValue)
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
      codeValue.value = sql
    }
    const getValue = () => {
      return codeValue.value
    }
    const calcBtnStyle = () => {
      if(!btnStyle.top || !btnStyle.left) {
        const containerWidthV = codeViewContainerRef.value?.getBoundingClientRect().width
        const displayNameheightV = displayNameRef.value?.getBoundingClientRect().height
        if(containerWidthV) {
          btnStyle.left = `${containerWidthV - 30}px`
        }
        if(displayNameheightV) {
          btnStyle.top = `${displayNameheightV + 15}px`
        }
      }
    }
    const onMouseEnterCodemirror = () => {
      calcBtnStyle()
      showBtn()
    }
    const onMouseOverBtn = () => {
      calcBtnStyle()
      showBtn()
    }
    const onMouseOverCard = () => {
      calcBtnStyle()
      showBtn()
    }
    const onMouseLeaveCard = () => {
      hideBtn()
    }
    const showBtn = () => {
      btnVisiable.value = true
    }
    const hideBtn = () => {
      btnVisiable.value = false
    }
    const copyCode = () => {
      iconName.value = ICON.CHECK
      toClipboard(codeValue.value).then(() => {
        setTimeout(() => {
          iconName.value = ICON.COPY
        }, 300)
      })
        .catch(() => {
          iconName.value = ICON.CHECK
        })
    }
    return {
      codeValue,
      codemirror,
      iconName,
      btnVisiable,
      codeViewContainerRef,
      displayNameRef,
      btnRef,
      btnStyle,
      extensions,
      setValue,
      getValue,
      getSelection,
      handleCodeMirrorFocus,
      onCodeMirrorInstantReady,
      onMouseEnterCodemirror,
      onMouseOverBtn,
      onMouseOverCard,
      onMouseLeaveCard,
      copyCode
    }
  }
})
</script>
<style scoped>
.code-block {
  font-family: Arial, monospace;
  font-size: 16px;
}
.el-button--mini {
  padding: 3px 5px !important;
}
.code-btn {
  position: absolute;
  height: 25px;
  width: 25px;
}
.code-view-container {
  position: relative;
}
.display-name-first {
  margin-bottom: 10px;
  text-align: left;
}
.display-name-unfirst {
  margin-bottom: 10px;
  margin-top: 10px;
  text-align: left;
}
</style>
