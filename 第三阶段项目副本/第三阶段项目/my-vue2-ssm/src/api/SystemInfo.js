import request from '@/utils/request' // 引入封装好的Axios实例

// 获取系统信息的接口
export function getSystemInfo() {
  // token 会由 request.js 拦截器自动从 localStorage 获取并添加到请求头
  return request({
    url: '/system/info',
    method: 'get'
  })
}