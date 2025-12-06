import request from '@/utils/request'

// 查询车牌列表
export function listCarPlate(query) {
  return request({
    url: '/carPlate/list',
    method: 'get',
    params: query
  })
}

// 查询车牌详细
export function getCarPlate(id) {
  return request({
    url: '/carPlate/detail/' + id,
    method: 'get'
  })
}

// 新增车牌
export function addCarPlate(data) {
  return request({
    url: '/carPlate/add',
    method: 'post',
    data: data
  })
}

// 修改车牌
export function updateCarPlate(data) {
  return request({
    url: '/carPlate/update',
    method: 'put',
    data: data
  })
}

// 删除车牌
export function delCarPlate(id) {
  return request({
    url: '/carPlate/delete/' + id,
    method: 'delete'
  })
}

// 导出车牌（如果需要）
export function exportCarPlate(query) {
  return request({
    url: '/carPlate/export',
    method: 'get',
    params: query
  })
}