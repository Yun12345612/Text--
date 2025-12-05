// src/api/adminApi.js
import request from '@/utils/request' // 引入封装好的Axios实例
// 管理员登录
export function adminLogin(data) {
  return request({
    url: '/admin/login',
    method: 'post',
    data: data,
  })
}

// 管理员注册
export function adminRegister(data) {
  return request({
    url: '/admin/register',
    method: 'post',
    data: data
  })
}

// 更新管理员状态
export function updateAdminStatus(data) {
  return request({
    url: '/admin/updateAdminStatus',
    method: 'post',
    data: data
  })
}

// 获取验证码（可选，如果前端需要单独调用）
export function getCaptcha() {
  return request({
    url: '/code',
    method: 'get',
    responseType: 'blob'
  })
}