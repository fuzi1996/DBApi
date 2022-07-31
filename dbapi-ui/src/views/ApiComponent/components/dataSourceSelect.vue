<template>
  <div :class='["form", size == "mini" ? "mini":""]'>
    <span class='label'>{{ (nullable ? '' : '*') + label }}</span>
    <div
      class='inputPP'
      :style='"width: " + width'
    >
      <input
        class='input'
        :value='getLabel()'
        readonly='readonly'
        @focus.stop='showOptions = true'
        @blur.stop='showOptions = false'
      >
      <div
        v-show='showOptions'
        class='options'
      >
        <div
          v-for='(item) in options'
          :key='item[optionLabel]'
          class='option'
          @mousedown='optionClick(item)'
        >
          {{ item[optionLabel] }}
        </div>
        <div class='bottom'>
          <slot />
        </div>
        <div
          v-if='options.length == 0'
          class='none'
        >暂无数据</div>
      </div>
      <el-icon
        class='select-arrow'
        :size='25'
      >
        <ArrowUp v-if='showOptions' />
        <ArrowDown v-else />
      </el-icon>
    </div>
  </div>
</template>
<script>
export default {
  name: 'DataSourceSelect',
  props: {
    modelValue: {
      type: [String,Number],
      default: ''
    },
    label: {
      type: String,
      default: ''
    },
    size: {
      type: String,
      default: ''
    },
    options: {
      type: Array,
      default: () => []
    },
    optionLabel: {
      type: String,
      default: 'label'
    },
    optionValue: {
      type: String,
      default: 'value'
    },
    width: {
      type: String,
      default: '200px'
    },
    nullable: {
      type: Boolean,
      default: true
    }
  },
  emits:['update:modelValue','optionClick','onchange'],
  data() {
    return {
      showOptions: false
    }
  },
  watch: {
    modelValue(val) {
      this.$emit('onchange', val)
    }
  },
  methods: {
    getLabel() {
      let p = ''
      this.options.forEach((elem) => {
        if (elem[this.optionValue] == this.modelValue) {
          p = elem[this.optionLabel]
        }
      })
      return p
    },
    optionClick(v) {
      this.showOptions = false
      this.$emit('update:modelValue', v[this.optionValue])
      this.$emit('optionClick', v)
    }
  }
}
</script>

<style lang="scss" scoped>


.form {
  display: inline-block;
  line-height: 40px;
  vertical-align: top;
  margin-right: 10px;
  margin-bottom: 5px;
  position: relative;
  font-family: Consolas, Arial, monospace;
  white-space: nowrap;


  .label {
    display: inline-block;
    background-color: #06B176;
    height: 40px;
    padding: 0 10px;
    color: #fff;
    border: 1px solid #a7a7a7;
    border-radius: 5px 0 0 5px;
  }

  .inputPP {
    width: 200px;
    display: inline-block;
    position: relative;
    vertical-align: top;

    .icon {
      position: absolute;
      right: 0px;
      top: 0px;
      line-height: 40px;
      padding-right: 5px;
    }

    .input {
      display: block;
      padding: 0 10px;
      width: 100%;
      height: 40px;
      //vertical-align: top;
      border: 1px solid #a7a7a7;
      border-left: 0px;
      outline: 0px solid #afecab;
      border-radius: 0 5px 5px 0;
      font-family: Consolas, Arial, monospace;

      &:focus {
        border: 1px solid #009933;
        border-left: 0px;
      }
    }

    .options {
      //border: 1px solid #009933;
      border-radius: 5px;
      width: 100%;
      position: absolute;
      right: 0;
      top: 42px;
      background-color: #fff;
      box-shadow: 0 0 3px #82848a;
      z-index: 1000;

      .option {
        margin: 2px 0;

        padding: 0px 10px;
        line-height: 30px;
        cursor: pointer;

        &:hover {
          //color: rgb(61, 192, 21);
          background-color: #f6f6f6;
        }
      }

      .none {
        text-align: center;
        color: #ccc;
      }
    }

    .bottom {
      //border-top: 1px solid #a7a7a7;
    }
  }


}

.mini {
  line-height: 26px;
  height: 26px;
  margin: 0;
  border-radius: 0;
  //border-width: 0;

  .label {
    height: 26px;
    border-radius: 0;
  }

  .inputPP {


    .options {
      top: 26px;
    }

    .input {
      height: 26px;
      border-radius: 0;
    }

    .icon {
      line-height: 26px;
    }
  }
}

.select-arrow{
  display: contents;
}
</style>
