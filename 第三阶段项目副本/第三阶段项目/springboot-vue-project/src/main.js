import Vue from 'vue'
import App from './App.vue'
import router from './router' 
import store from './store'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css' // 必须添加这一行！

Vue.use(ElementUI) // 全局注册Element UI组件

new Vue({
  store,
  router,
  render: h => h(App),
}).$mount('#app')