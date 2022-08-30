<template>
  <div style='display: inline-block'>
    <i v-if='type =="mysql"' class='iconfont icon-my-SQL db' />
    <i v-if='type =="postgresql"' class='iconfont icon-postgresql db' />
    <i v-if='type =="hive"' class='iconfont icon-hive db' />
    <i v-if='type =="sqlserver"' class='iconfont icon-SQLServer db' />
    <i v-if='type =="clickhouse"' class='iconfont icon-clickhouse2 db' />
    <i v-if='type =="kylin"' class='iconfont icon-Kylin db' />
    <i v-if='type =="oracle"' class='iconfont icon-oracle db' />
    <i v-if='type =="TDengine"' class='iconfont icon-TDengine db' />
    <i v-if='type =="doris"' class='iconfont icon-doris db' />
    <i v-if='type =="oceanbase"' class='iconfont icon-oceanbase db' />
    <i v-if='type =="greenplum"' class='iconfont icon-GreenPlum db' />
    <i v-if='type =="others"' class='iconfont icon-OTHER db' />
  </div>
</template>

<script>
export default {
  name: 'DbIcon',
  props:{
    type:{
      type:String,
      default: ''
    }
  }
}
</script>

<style scoped>
.db {
  color: #0698a5;
  font-size: 18px;
  font-weight: 500;
  margin-right: 5px;
}
</style>