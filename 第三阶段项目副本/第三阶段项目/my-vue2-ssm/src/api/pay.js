import request from '@/utils/request'

// 支付宝支付接口 -
export function alipayPay(orderData) {
  return request({
    url: '/alipay/pay', 
    method: 'post',  
    data: orderData,  
    responseType: 'text'
  })
}

// 查询订单状态
export function checkOrderStatus(traceNo) {
  return request({
    url: '/order/status',
    method: 'get',
    params: { traceNo }
  })
}