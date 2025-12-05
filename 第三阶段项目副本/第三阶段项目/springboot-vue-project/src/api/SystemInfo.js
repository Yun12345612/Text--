import request from '@/utils/request' // 引入封装好的Axios实例

// 获取系统信息的接口
export function getSystemInfo() {
  // 从localStorage获取登录时存储的token
  const token = localStorage.getItem('token')
  return request({
    url: '/system/info',
    method: 'get',
    params: {
      'X-Token': token // 传递token给后端（与后端request.getParameter("X-Token")对应）
    }
  })
}