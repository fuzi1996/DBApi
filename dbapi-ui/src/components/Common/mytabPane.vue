<template>
  <div v-show='active'>
    <slot />
  </div>
</template>
<script>
export default {
  name: 'MytabPane',
  props: {
    label: {
      type: String,
      default: null
    },
    value: {
      type: String,
      default: null
    }
  },
  data() {
    return {
      flag: false
    }
  },
  computed: {
    active() {
      const active = this.$parent.currentName === this.label
      return active
    }
  }
}
</script>
<style scoped>
</style>

