<template>
  <codemirror
    ref='codemirror'
    v-model='jsonParam'
    :autofocus='true'
    :indent-with-tab='true'
    :tab-size='2'
    :extensions='extensions'
    :style='codeMirrorStyle'
    @blur='handleCodeMirrorBlur'
  />
</template>
<script lang="ts" setup>
import { ref,defineProps,defineEmits,onMounted,defineExpose } from 'vue'
import { linter } from '@codemirror/lint'
import { json,jsonParseLinter } from '@codemirror/lang-json'

const props = defineProps<{
  codeMirrorStyle: {
    type: Record<string, unknown>,
    default: { height: '200px', }
  }
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
}>()

const extensions = [json(),linter(jsonParseLinter())]
const jsonParam = ref('')



const setData = (modelValue: string) => {
  if(modelValue) {
    jsonParam.value = modelValue
  }else{
    jsonParam.value = '{}'
    emit('update:modelValue',jsonParam.value)
  }
}

defineExpose({ setData })

const handleCodeMirrorBlur = () => {
  emit('update:modelValue',jsonParam.value)
}
</script>
