// src/api/login.js
import request from '@/utils/request.js'


// 登录方法
export function login(account,password) {
  return request({
    url: '/car/user/login',
    method: 'post',
    data:account,password
  })
}

// 退出登录
export function logout() {
  return request({
    url: '/logout',
    method: 'post'
  })
}

// 获取用户信息
export function getInfo() {
  return request({
    url: '/car/user/getInfo',
    method: 'get'
  })
}

// 获取验证码
export function getCodeImg() {
  return request({
    url: '/captchaImage',
    headers: {
      isToken: false
    },
    method: 'get',
    timeout: 20000
  })
}
// 用户注册
export function register(userData) {
  return request({
    url: '/car/user/register',
    method: 'post',
    data: userData
  })
}
