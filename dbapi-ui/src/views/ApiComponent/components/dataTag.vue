<template>
  <div class='tag-item'>
    <span class='left'>{{ name }}</span>
    <span class='right'>{{ type }}</span>
  </div>
</template>

<script>
export default {
  name: 'DataTag',
  props: {
    name:{
      type: String,
      default: ''
    },
    type:{
      type: String,
      default: ''
    }
  }
}
</script>

<style scoped>
.tag-item {
  margin: 0 4px 2px 0;
  display: inline-block;
}

.tag-item span {
  font-size: 12px;
  line-height: 12px;
  color: cornsilk;
}

.left {
  padding: 4px 4px 4px 6px;
  background-color: #40485b;
  border-radius: 4px 0 0 4px;
}

.right {
  padding: 4px 6px 4px 4px;
  background-color: #2eb486;
  border-radius: 0 4px 4px 0;
}
</style>
