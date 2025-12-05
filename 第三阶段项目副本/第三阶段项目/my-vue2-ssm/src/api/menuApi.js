import request from '@/utils/request' // 引入封装好的Axios实例

export function getPermissionMenu(adminId) {
  return request({
    url: '/menu/permission-tree',
    method: 'get',
    params: { adminId }
  })
}