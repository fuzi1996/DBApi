<template>
  <el-tag
    v-for='tag in dynamicTags'
    :key='tag.id'
    class='mx-1'
    closable
    :disable-transitions='false'
    @close='handleDeleteDynamicTag(tag.id)'
  >
    {{ tag.name }}
  </el-tag>
  <el-input
    v-if='inputVisible'
    ref='InputRef'
    v-model='inputValue'
    maxlength='10'
    show-word-limit
    size='small'
    class='api-group-input'
    @keyup.enter='handleInputConfirm'
    @blur='handleInputConfirm'
  />
  <el-button
    v-else
    class='button-new-tag ml-1'
    size='small'
    @click='showApiGroupInput'
  >
    + New Group
  </el-button>
</template>

<script lang="ts" setup>
import { nextTick, ref, onMounted } from 'vue'
import { ElInput } from 'element-plus'
import { listAllGroup,IGroupItem,createGroup,deleteGroup } from '/@/api/api/group'
import { ElMessage } from 'element-plus'

const inputValue = ref<string>('')
const dynamicTags = ref<IGroupItem[]>([])
const inputVisible = ref<boolean>(false)
const InputRef = ref<InstanceType<typeof ElInput>>()

onMounted(() => {
  loadAllGroup()
})

const loadAllGroup = () => {
  listAllGroup().then(({ data }) => {
    dynamicTags.value.splice(0)
    dynamicTags.value.push(...data.data)
  })
}

const handleDeleteDynamicTag = (id: string) => {
  deleteGroup(id).then(({ data:response }) => {
    if(response.success) {
      const index = dynamicTags.value.findIndex(item => item.id === id)
      dynamicTags.value.splice(index,1)
    }else{
      ElMessage.info(response.msg)
    }
  })
}

const showApiGroupInput = () => {
  inputVisible.value = true
  nextTick(() => {
    InputRef.value!.input!.focus()
  })
}

const handleInputConfirm = () => {
  if (inputValue.value) {
    createGroup(inputValue.value).then(() => {
      loadAllGroup()
    })
  }
  inputVisible.value = false
  inputValue.value = ''
}
</script>
<style scoped>
.api-group-input{
  width: 120px;
}
</style>