import Vue from 'vue'
import VueRouter from 'vue-router'


Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import( '../views/Login.vue')
  },
  {
    path: '/home',
    name: 'Home',
      component: () => import( '../views/Home.vue')
    },
   {
    path: '/register',
    name: 'register',
    component: () => import( '../views/register.vue')
  },
   {
    path: '/PersonalCenter',
    name: 'PersonalCenter',
    component: () => import( '../views/PersonalCenter.vue')
  },
  {
    path: '/order',
    name: 'order',
    component: () => import( '../views/order.vue')
  },
  {
    path: '/car',
    name: 'car',
    component: () => import( '../views/car.vue')
  },
   {
    path: '/parkingEsmap',
    name: 'parkingEsmap',
    component: () => import( '../views/parkingEsmap.vue')
  } 
]


const router = new VueRouter({
  mode: 'history',
  routes
})

export default router
