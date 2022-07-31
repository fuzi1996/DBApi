import { createI18n } from 'vue-i18n'
import messages from './langs'

const i18n = createI18n({
  locale: 'cn',
  legacy:false,
  messages
})

export default i18n
