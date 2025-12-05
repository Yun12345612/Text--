import Vue from 'vue'
import Router from 'vue-router'
import LoginView from '@/components/LoginView.vue'
import RegisterView from '@/components/RegisterView.vue'
import PackagesView from '@/components/PackagesView.vue'
import HomeView from '@/components/HomeView.vue'
import ExaminationView from '@/components/ExaminationView.vue'
import BuyView from '@/components/BuyView.vue'
import OrderView from '@/components/OrderView.vue'
import TijianBaogaoView from '@/components/TijianBaogaoView.vue'

Vue.use(Router)

// 使用懒加载
const BasicLayout = () => import('@/components/BasicLayout.vue')
const Home = () => import('@/components/HomeView.vue')

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: LoginView
  },
  {
    path: '/register',
    name: 'Register',
    component: RegisterView
  },
  {
    path: '/',
    component: BasicLayout,  // 使用布局组件
    children: [
      {
        path: '',
        name: 'Home',
        component: HomeView  // 首页组件
      },
      {
        path: 'packages',
        name: 'Packages',
        component: PackagesView  // 套餐页面
      },
      {
        path: 'examination',
        name: 'Examination',
        component: ExaminationView  // 项目页面
      },
      {
        path: '/buy',
        name: 'Buy',
        component: BuyView  // 购买页面
      },
       {
        path: '/order',
        name: 'Order',
        component: OrderView  // 订单页面
      },
      {
        path: '/reports',
        name: 'Tijian',
        component: TijianBaogaoView  // 订单页面
      },
      
    ]
  }
]
//告诉路由器如何工作
export default new Router({
  mode: "history",
  routes
})