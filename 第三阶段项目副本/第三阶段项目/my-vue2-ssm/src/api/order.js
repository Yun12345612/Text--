// 引入封装好的request实例
import request from '@/utils/request'

/**
 * 订单管理API
 */

// 1. 创建订单接口（对应后端 POST /order/create）
export const createOrder = (orderData) => {
  return request({
    method: 'POST',
    url: '/order/create',
    data: orderData
  })
}

// 2. 获取所有订单接口（对应后端 GET /order/all）
export const getAllOrders = () => {
  return request({
    method: 'GET',
    url: '/order/all'
  })
}

// 3. 查询订单状态接口（对应后端 GET /order/status）
export const checkOrderStatus = (orderNo) => {
  return request({
    method: 'GET',
    url: '/order/status',
    params: { traceNo: orderNo }
  })
}

// 4. 按类型查询订单接口（对应后端 GET /order/type/{orderType}）
export const getOrdersByType = (orderType) => {
  return request({
    method: 'GET',
    url: `/order/type/${orderType}`
  })
}

// 5. 复杂条件查询订单接口（对应后端 POST /order/search）
export const searchOrders = (searchParams) => {
  return request({
    method: 'POST',
    url: '/order/search',  // 修正URL，去掉多余的斜杠
    data: searchParams
  })
}

// 6. 简单条件查询订单列表接口（对应后端 GET /order/list）
export const getOrderList = (queryParams) => {
  return request({
    method: 'GET',
    url: '/order/list',
    params: queryParams
  })
}

// 7. 更新订单状态接口（对应后端 POST /order/updateStatus）
export const updateOrderStatus = (statusData) => {
  return request({
    method: 'POST',
    url: '/order/updateStatus',
    data: statusData
  })
}

// 8. 删除订单接口（根据ID）（对应后端 DELETE /order/delete/{orderId}）
export const deleteOrderById = (orderId) => {
  return request({
    method: 'DELETE',
    url: `/order/delete/${orderId}`
  })
}

// 9. 删除订单接口（根据追踪号）（对应后端 DELETE /order/deleteByTraceNo/{traceNo}）
export const deleteOrderByTraceNo = (traceNo) => {
  return request({
    method: 'DELETE',
    url: `/order/deleteByTraceNo/${traceNo}`
  })
}

// ========== 新增的接口结束 ==========

// 注意：您后端没有以下接口，需要先在后端添加或删除这些API调用
// export const addOrder = (orderData) => {
//   return request({
//     method: 'POST',
//     url: '/order/add',
//     data: orderData
//   })
// }

// export const updateOrder = (orderData) => {
//   return request({
//     method: 'POST',
//     url: '/order/update',
//     data: orderData
//   })
// }

// export const deleteOrder = (orderId) => {
//   return request({
//     method: 'POST',
//     url: '/order/delete',
//     data: { id: orderId }
//   })
// }