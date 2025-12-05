import request from '@/utils/request'; // 引入封装好的Axios实例

// 对应体检套餐相关接口
export function examinationGetById(id) {
  return request({
    url: `/api/examination-packages/${id}`,
    method: 'get'
  });
}

export function examinationGetAll() {
  return request({
    url: '/api/examination-packages',
    method: 'get'
  });
}

export function examinationAdd(data) {
  return request({
    url: '/api/examination-packages',
    method: 'post',
    data: data
  });
}

export function examinationUpdate(data) {
  return request({
    url: '/api/examination-packages',
    method: 'put',
    data: data
  });
}

export function examinationDelete(id) {
  return request({
    url: `/api/examination-packages/${id}`,
    method: 'delete'
  });
}
export function examinationPage(params) {
  return request({
    url: '/api/examination-packages/page', 
    method: 'get', 
    params: params 
  })
}
// 获取所有项目列表（用于穿梭框）
export function getItemList(params) {
  return request({
    url: '/examination/item/list',
    method: 'get',
    params // 传递查询参数（如itemName、departmentId、价格区间、分页参数等）
  });
}