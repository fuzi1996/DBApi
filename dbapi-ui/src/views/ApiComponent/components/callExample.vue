<template>
  <div>
    <code-view
      ref='shell'
      lang-display-name='shell'
      :model-value='shellCode'
      :mode='LANGUAGE.SHELL'
      :is-first='true'
    />
    <code-view
      ref='javascript'
      lang-display-name='javascript'
      :model-value='javaScriptCode'
      :mode='LANGUAGE.JAVASCRIPT'
    />
    <code-view
      ref='python'
      lang-display-name='python'
      :model-value='pythonCode'
      :mode='LANGUAGE.PYTHON'
    />
    <code-view
      ref='go'
      lang-display-name='go'
      :model-value='goCode'
      :mode='LANGUAGE.GO'
    />
    <code-view
      ref='java'
      lang-display-name='java'
      :model-value='javaCode'
      :mode='LANGUAGE.JAVA'
    />
  </div>
</template>

<script>
import codeView from './codeView.vue'
import { generateCallExampleCode } from '/@/utils/code'
import { LANGUAGE } from '/@/constant/language'
export default {
  name: 'CallExample',
  components: {
    codeView
  },
  props: {
    // 请求地址 "127.0.0.1:8520/api"
    address: {
      type: String,
      default: ''
    },
    /**
      {
        "path": "test",
        "params": [
          {
            "name": "id",
            "type": "string",
            "values": [
              {
                "va":"a"
              },
              {
                "va":"b"
              }
            ]
          }
        ],
        "previlege": 1,
        "jsonParam": null,
        "sqlList": [
          {
            "sqlText": "-- 请输入sql，一个标签只能输入一条sql\\nselect * from user\\n\\n<where>\\nid = #{id}\\n</where>",
            "transformPlugin": null,
            "transformPluginParams": null
          }
        ],
        "contentType": "application/x-www-form-urlencoded",
        "token": null,
      }
     */
    detail: {
      type: Object,
      default: () => undefined
    }
  },
  data() {
    return {
      LANGUAGE:Object.freeze(LANGUAGE),
      shellCode: '',
      javaScriptCode: '',
      pythonCode: '',
      goCode: '',
      javaCode: ''
    }
  },
  methods: {
    initCallExampleCode() {
      this.shellCode = generateCallExampleCode(
        this.generateOtherParam(LANGUAGE.SHELL)
      )
      this.javaScriptCode = generateCallExampleCode(
        this.generateOtherParam(LANGUAGE.JAVASCRIPT)
      )
      this.pythonCode = generateCallExampleCode(
        this.generateOtherParam(LANGUAGE.PYTHON)
      )
      this.goCode = generateCallExampleCode(
        this.generateOtherParam(LANGUAGE.GO)
      )
      this.javaCode = generateCallExampleCode(
        this.generateOtherParam(LANGUAGE.JAVA)
      )
    },
    generateOtherParam(lang) {
      return {
        address: this.address,
        detail: this.detail,
        lang
      }
    },
    refresh() {
      console.log('refresh')
      this.initCallExampleCode()
      Object.keys(this.$refs).forEach((refName) => {
        if (this.$refs[refName]?.refresh) {
          this.$refs[refName].refresh()
        }
      })
    }
  }
}
</script>

<style scoped>
</style>