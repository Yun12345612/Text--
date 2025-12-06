import request from '@/utils/request'; // 封装的 axios 实例

// 拉取用户订单列表
export function fetchOrders(params) {
  return request({
    url: '/order/list',
    method: 'get',
    params,
  });
}

// 触发支付宝支付
export function payOrder(data) {
  return request({
    url: '/aliApy/pay', // 后端支付接口地址
    method: 'post',
    data,
  });
}
// 3.软删除订单
export function softDeleteOrder(id) {
  return request({
    url: `/order/${id}`,
    method: 'delete'
  })
}

export function getBalance(userId) {
  return request({
    url: '/user/balance', 
    method: 'get',
    params: { userId }
  });
}

// 1.查询车位列表
export function listParking(query) {
  return request({
    url: '/car/list',
    method: 'get',
    params: query
  })
}

// 6. 车辆出场接口（调用后端 /order/exit/{orderId}，核心：计算停车费用）
export function carExit(orderId) {
  return request({
    url: `/order/exit/${orderId}`, // 对应后端出场结算接口
    method: 'post' // 后端是 @PostMapping，这里必须用 post
  });
}

// 7. 查询最新未支付订单（调用后端 /order/getLatestUnpaidOrder，避免订单信息过时）
export function getLatestUnpaidOrder(carNumber) {
  return request({
    url: '/order/getLatestUnpaidOrder', // 对应后端查询最新未支付订单接口
    method: 'get',
    params: { carNumber } // 传车牌号，查该车牌的最新未支付订单
  });
}