// src/api/admin.js
import request from '@/utils/request'; // 引入封装好的Axios实例

// 1. 管理员登录
export function adminLogin(data) {
  return request({
    url: '/admin/login',
    method: 'post',
    data: data
  });
}

// 2. 管理员注册
export function adminRegister(data) {
  return request({
    url: '/admin/register',
    method: 'post',
    data: data
  });
}

// 3. 管理员信息修改
export function adminUpdate(data) {
  return request({
    url: '/admin/update',
    method: 'post',
    data: data
  });
}

// 4. 管理员列表查询
export function adminList(params) {
  return request({
    url: '/admin/list',
    method: 'get',
    params: params
  });
}

// 5. 获取验证码（带时间戳防止缓存）
export function getCode() {
  return request({
    url: '/get/code',
    method: 'get',
    params: {
      timestamp: new Date().getTime() // 每次请求添加时间戳，避免浏览器缓存
    }
  });
}
// 6. 软删除管理员
export function adminDelete(id) {
  return request({
    url: `/admin/delete/${id}`, // 拼接路径参数 id
    method: 'delete'
  });
}
//新增管理员
export function adminAdd(data) {
  return request({
    url: '/admin/add',
    method: 'post',
    data: data
  });
}