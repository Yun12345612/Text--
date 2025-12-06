// src/api/car/parking.js
import request from '@/utils/request'

// 1.查询车位列表
export function listParking(query) {
  return request({
    url: '/car/list',
    method: 'get',
    params: query
  })
}

// 随机查询车位空闲且启用车位
export function randomParking(query) {
  return request({
    url: '/car/randomPark',
    method: 'get',
    params: query
  })
}

// 查询车位详细
export function getParking(id) {
  return request({
    url: '/car/parking/' + id,
    method: 'get'
  })
}

// 新增车位
export function addParking(data) {
  return request({
    url: '/car/add',
    method: 'post',
    data: data
  })
}

 // 修改车位
export function updateParking(data) {
  return request({
    url: '/car/updateParking',
    method: 'put',
    data: data
  })
} 

// 删除车位
export function deleteParking(ids) {
  return request({
    url: '/car/parking/' + ids,
    method: 'delete'
  })
}

// 导出车位
export function exportParking(query) {
  return request({
    url: '/car/parking/export',
    method: 'get',
    params: query
  })
}

// 车位状态修改
export function updateParkingStatus(data) {
  // const data = {
  //   id,
  //   parking_status
  // }
  return request({
    url: '/car/changeStatus',
    method: 'put',
    data: data
  })
}