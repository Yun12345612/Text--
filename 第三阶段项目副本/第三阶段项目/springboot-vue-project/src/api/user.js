import request from '@/utils/request'; // 引入封装好的Axios实例

//1.查询用户列表
export function getUserList(params) {
  return request({
    url: '/user/getUserList',
    method: 'get',
    params: params
  });
}
//2.新增用户
export function userAdd(data) {
  return request({
    url: '/user/adduser',
    method: 'post',
    data: data
  });
}
//3.更新用户信息
export function userUpdate(data) {
  return request({
    url: '/user/updateuser',
    method: 'put',
    data: data
  });
}
//4.软删除用户
export function userDelete(userId) {
  return request({
    url: `/user/delete/${userId}`,
    method: 'delete'
  });
}