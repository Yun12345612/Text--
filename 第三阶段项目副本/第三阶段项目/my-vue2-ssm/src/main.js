import Vue from 'vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import App from './App.vue'
import router from './router'
import perssion from './perssion'
import store from './store/index.js' // 引入创建好的 store
import Vuex from 'vuex' // 引入 Vuex

Vue.use(Vuex) // 正确使用 Vuex
Vue.use(ElementUI)
//忽略所有重复导航错误
router.push = function (location) {
  return this.replace(location)
}
new Vue({
  store,
  perssion,
  router,
  render: h => h(App)
}).$mount('#app')