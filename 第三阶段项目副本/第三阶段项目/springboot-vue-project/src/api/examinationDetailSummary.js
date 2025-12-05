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


//3. 根据订单号查询体检细项小结
export function getExaminationDetailsByOrder(orderNo) {
  return request({
    url: `/examination-detail-summary/get-by-order/${orderNo}`,
    method: 'get'
  })
}
// 根据细项ID查询小结
export function getExaminationDetailsByDetailId(detailId) {
  return request({
    url: `/examination-detail-summary/get-by-detail/${detailId}`,
    method: 'get'
  })
}

// 根据管理员ID查询小结
export function getExaminationDetailsByAdminId(adminId) {
  return request({
    url: `/examination-detail-summary/get-by-admin/${adminId}`,
    method: 'get'
  })
}

// 根据ID查询单条小结
export function getExaminationDetailById(id) {
  return request({
    url: `/examination-detail-summary/get-by-id/${id}`,
    method: 'get'
  })
}