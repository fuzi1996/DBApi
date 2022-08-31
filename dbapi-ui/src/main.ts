import { createApp } from 'vue'
import App from '/@/App.vue'
import ElementPlus,{ ElMessage } from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import direct from '/@/directive/index'
import router from '/@/router/index'
import { pinia } from '/@/store'
import i18n from './i18n'
import '/@/permission'
import '/@/assets/css/index.css'

import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/display.css'

import 'nprogress/nprogress.css'
import 'virtual:svg-icons-register'
import SvgIcon from '/@/components/SvnIcon/index.vue'
import dbicon from '/@/components/Common/dbIcon.vue'
import * as ElIcons from '@element-plus/icons-vue'
import '/@/icons/iconfont.css'
import VueCodemirror from 'vue-codemirror'

const app = createApp(App)
direct(app)
app.use(ElementPlus)
app.use(router)
app.use(pinia)
app.use(i18n)
app.component('SvgIcon', SvgIcon)

app.config.globalProperties.$message = ElMessage

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
const ElIconsData = ElIcons as unknown as Array<() => Promise<typeof import('*.vue')>>
for (const iconName in ElIconsData) {
  app.component(`ElIcon${iconName}`, ElIconsData[iconName])
}

app.component('DbIcon',dbicon)

app.use(VueCodemirror, {
  autofocus: true,
  disabled: false,
  indentWithTab: true,
  tabSize: 2
})

app.mount('#app')
