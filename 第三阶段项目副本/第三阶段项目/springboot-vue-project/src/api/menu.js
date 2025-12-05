import request from '@/utils/request' // 引入封装好的Axios实例

export const getPermissionMenu = (roleId) => {
  return request({
    url: '/tree/menus', 
    method: 'get', 
    params: { roleId } 
  });
}