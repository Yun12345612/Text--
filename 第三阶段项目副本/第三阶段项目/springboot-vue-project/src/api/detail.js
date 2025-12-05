import request from '@/utils/request'

/**
 * 新增检查项目明细
 * @param {Object} detail 检查项目明细实体
 * @returns {Promise}
 */
export function addExaminationItemDetail(detail) {
  return request({
    url: '/examination-item-detail',  
    method: 'post',
    data: detail
  })
}

/**
 * 软删除检查项目明细
 * @param {Number} detailId 检查项目明细ID
 * @returns {Promise}
 */
export function deleteExaminationItemDetail(detailId) {
  return request({
    url: `/examination-item-detail/${detailId}`,  
    method: 'delete'
  })
}

/**
 * 根据ID查询检查项目明细
 * @param {Number} detailId 检查项目明细ID
 * @returns {Promise}
 */
export function getExaminationItemDetailById(detailId) {
  return request({
    url: `/examination-item-detail/${detailId}`,  
    method: 'get'
  })
}

/**
 * 查询所有检查项目明细（未删除的）
 * @returns {Promise}
 */
export function getAllExaminationItemDetails() {
  return request({
    url: '/examination-item-detail/all',  
    method: 'get'
  })
}

/**
 * 分页查询检查项目明细
 * @param {Object} params 分页参数
 * @param {Number} params.pageNum 页码，默认第1页
 * @param {Number} params.pageSize 每页大小，默认10条
 * @returns {Promise}
 */
export function getExaminationItemDetailsByPage(params) {
  return request({
    url: '/examination-item-detail/page',  
    method: 'get',
    params: params
  })
}

/**
 * 更新检查项目明细
 * @param {Object} detail 检查项目明细实体
 * @returns {Promise}
 */
export function updateExaminationItemDetail(detail) {
  return request({
    url: '/examination-item-detail',  
    method: 'put',
    data: detail
  })
}

/**
 * 根据条件查询检查项目明细
 * @param {Object} condition 查询条件
 * @returns {Promise}
 */
export function getExaminationItemDetailsByCondition(condition) {
  return request({
    url: '/examination-item-detail/condition', 
    method: 'post',
    data: condition
  })
}

/**
 * 统计检查项目明细总数（未删除的）
 * @returns {Promise}
 */
export function getExaminationItemDetailCount() {
  return request({
    url: '/examination-item-detail/count', 
    method: 'get'
  })
}

/**
 * 动态获取科室 - 这个也需要检查路径
 * @returns {Promise}
 */
export function getDepartmentList() {
  return request({
    url: '/department/gteList', 
    method: 'get'
  });
}