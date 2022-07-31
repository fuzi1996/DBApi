<template>
  <div class='table-search flex flex-col'>
    <div
      ref='searchEl'
      :class='{"table-search-form":isEnableAdvanceSearch}'
    >
      <slot name='search' />
    </div>
        
    <div class='flex justify-between items-center mb-2'>
      <slot name='button-group' />
      <el-button
        v-if='isEnableAdvanceSearch'
        type='text'
        @click='toggleSearch'
      >
        高级搜索
        <el-icon>
          <el-icon-arrow-up v-if='isShow' />
          <el-icon-arrow-down v-else />
        </el-icon>
      </el-button>
    </div>
    <slot />
    <el-pagination
      :current-page='currentPage'
      :page-sizes='[10, 20, 50, 100]'
      :page-size='pageSize'
      layout='total, sizes, prev, pager, next, jumper'
      :total='total'
      @size-change='handleSizeChange'
      @current-change='handleCurrentChange'
    />
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, SetupContext } from 'vue'
import { slide } from '/@/utils/animate'
export default defineComponent({
  name: 'TableSearch',
  props: {
    currentPage: {
      type: Number,
      default: 1
    },
    pageSize: {
      type: Number,
      default: 10
    },
    total: {
      type: Number,
      default: 0
    },
    isEnableAdvanceSearch:{
      type:Boolean,
      default: false
    }
  },
  emits: ['size-change', 'current-change', 'isShow-change'],
  setup(props, context: SetupContext) {
    const isShow = ref(false)
    const handleSizeChange = (v:any) => context.emit('size-change', v)
    const handleCurrentChange = (v: any) => context.emit('current-change', v)
    const toggleSearch = () => {
      isShow.value = !isShow.value
      slide(searchEl, isShow.value)
      context.emit('isShow-change',isShow.value)
    }
    const searchEl = ref(null)
    return {
      isShow,
      handleSizeChange,
      handleCurrentChange,
      searchEl,
      toggleSearch
    }
  }
        
})
</script>

<style lang="postcss" scoped>
.table-search-form {
    overflow: hidden;
    height: 0;
}
</style>