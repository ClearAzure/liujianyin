import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import { Icon } from '@iconify/vue'
import App from './App.vue'
import router from './router'
import './assets/styles.css'

const app = createApp(App)
// 创建 Pinia 实例,将 Pinia 实例注册到 Vue 应用中
app.use(createPinia())
app.use(router)
app.use(ElementPlus, { locale: zhCn })

app.component('Icon', Icon)

// 给 Vue 应用注册一个叫 Icon 的组件，以后整个项目里都可以直接用 <Icon />。
app.mount('#app')
