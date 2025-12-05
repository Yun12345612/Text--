import request from '@/utils/request' // 引入封装好的Axios实例

// 1. 保存细项小结
export function saveExaminationDetail(data) {
  return request({
    url: '/examination-detail-summary/save',
    method: 'post',
    data
  });
}

// 2. 保存细项小结（包含完整信息）
export function saveExaminationDetailWithDoctor(data) {
  return request({
    url: '/examination-detail-summary/save-with-doctor',
    method: 'post',
    data
  });
}



// 根据订单号查询体检细项小结
export function getExaminationDetailsByOrder(orderNo) {
  return request({
    url: `/examination-detail-summary/get-by-order/${orderNo}`,
    method: 'get'
  })
}

