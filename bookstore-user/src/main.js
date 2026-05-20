import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'

// 入口：创建 Vue 应用，挂载 Pinia 和路由
const app = createApp(App)
app.use(createPinia())
app.use(router)
app.mount('#app')
