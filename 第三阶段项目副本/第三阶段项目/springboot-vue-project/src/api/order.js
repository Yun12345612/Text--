
import request from '@/utils/request'; // 引入封装好的Axios实例
// 1.分页多条件查询体检项目
export function getItemList(params) {
    return request({
        url: '/api/examination/item/list',
        method: 'get',
        params // 传递查询参数（如itemName、departmentId、价格区间、分页参数等）
    });
}
// 2.对应体检套餐相关接口
export function examinationGetById(id) {
    return request({
        url: `/api/examination-packages/${id}`,
        method: 'get'
    });
}
//3.查询用户列表
export function getUserList(params) {
  return request({
    url: '/api/user/getUserList',
    method: 'get',
    params: params
  });
}


/**
 * 订单细项查询API
 * 提供订单细项相关的数据查询接口
 */

// 1. 根据订单编号路径参数查询细项详情
export function getOrderDetailsByPath(orderNo) {
  return request({
    url: `/order/details/${orderNo}`, // 路径参数方式
    method: 'get'
  });
}

// 2. 根据订单编号查询参数查询细项详情
export function getOrderDetailsByParam(orderNo) {
  return request({
    url: '/order/details', // 查询参数方式
    method: 'get',
    params: {
      orderNo: orderNo
    }
  });
}

