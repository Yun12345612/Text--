import Vue from 'vue'
import Router from 'vue-router'
// 用懒加载
// import Login from '@/components/LoginView.vue'
// import Register from '@/components/RegisterView.vue'
// import HomeView from '@/components/HomeView.vue'
// import DepartmentManagement from '@/components/DepartmentManagement.vue'
// import Dashboard from '@/components/Dashboard.vue' 
// import AdminView from '@/components/AdminView.vue'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'Login',

      component: () => import('@/components/LoginView.vue')
    },
    {
      path: '/register',
      name: 'Register',

      component: () => import('@/components/RegisterView.vue')
    },
    {
      path: '/home',
      name: 'Home',
      component: () => import('@/components/HomeView.vue'),
      redirect: '/home/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'HomeDashboard',
          component: () => import('@/components/HomeDashboard.vue'),
          meta: {
            title: '首页',
            icon: 'el-icon-s-home'
          }
        },
        {
          path: 'user',
          name: 'User',
          component: () => import('@/components/UserView.vue'),
          meta: {
            title: '用户管理',
            icon: 'el-icon-building'
          }
        },
        {
          path: 'department',
          name: 'DepartmentManagement',

          component: () => import('@/components/DepartmentManagement.vue'),
          meta: {
            title: '科室管理',
            icon: 'el-icon-building'
          }
        },
        {
          path: 'detail',
          name: 'ExaminationItemDetaiView',

          component: () => import('@/components/ExaminationItemDetaiView.vue'),
          meta: {
            title: '细项管理管理',
            icon: 'el-icon-building'
          }
        },
        {
          path: 'admin',
          name: 'AdminView',

          component: () => import('@/components/AdminView.vue'),
          meta: {
            title: '管理员管理',
            icon: 'el-icon-user-solid'
          },
        },
        {
          path: 'role',
          name: 'RoleView',
          component: () => import('@/components/RoleView.vue'),
          meta: {
            title: '角色管理',
            icon: 'el-icon-user-solid'
          },
        },
        {
          path: 'examination/item',
          name: 'ExaminationItem',

          component: () => import('@/components/ExaminationView.vue'),
          meta: {
            title: '体检项目管理',
            icon: 'el-icon-s-grid'
          }
        },
        {
          path: 'examination/package',
          name: 'PackageItem',

          component: () => import('@/components/PackageView.vue'),
          meta: {
            title: '体检套餐管理',
            icon: 'el-icon-s-grid'
          }
        },
        {
          path: 'info',
          name: 'UserInfo',
          component: () => import('@/components/UserInfoView.vue'),
          meta: {
            title: '体检信息管理',
            icon: 'el-icon-s-grid'
          }
        },
        {
          path: 'tijain',
          name: 'Tijian',
          component: () => import('@/components/TijianView.vue'),
          meta: {
            title: '体检结果查询',
            icon: 'el-icon-s-grid'
          }
        },
          {
          path: 'examination/input',
          name: 'InputField',
          component: () => import('@/components/InputFieldView.vue'),
          meta: {
            title: '体检小结录入',
            icon: 'el-icon-s-grid'
          }
        },
        {
          path: 'examination/summary-report',
          name: 'Zojie',
          component: () => import('@/components/ZojieView.vue'),
          meta: {
            title: '体检总结接收',
            icon: 'el-icon-s-grid'
          }
        },
        {
          path: 'order/my',
          name: 'UserBilling',
          component: () => import('@/components/UserBillingView.vue'),
          meta: {
            title: '体检开单',
            icon: 'el-icon-s-grid'
          }
        },
      ]
    }
  ],
})