// src/api/department.js
import request from '@/utils/request'; // 引入封装好的Axios实例

/**
 * 获取科室列表（分页）
 * @param {Object} params - 分页和搜索参数
 * @param {number} params.currentPage - 当前页码
 * @param {number} params.pageSize - 每页条数
 * @param {string} [params.name] - 科室名称（搜索条件）
 * @returns {Promise} - 请求Promise
 */
export function getDeptList(params) {
  return request({
    url: '/department/gteList', 
    method: 'get',
    params
  });
}

/**
 * 新增科室
 * @param {Object} data - 科室信息
 * @param {string} data.name - 科室名称
 * @param {string} data.location - 位置
 * @param {number} data.status - 状态（1-启用，0-禁用）
 * @returns {Promise} - 请求Promise
 */
export function addDept(data) {
  return request({
    url: '/department/add',
    method: 'post',
    data
  });
}

/**
 * 更新科室信息
 * @param {Object} data - 科室信息（含ID）
 * @param {number} data.id - 科室ID
 * @param {string} data.name - 科室名称
 * @param {string} data.location - 位置
 * @param {number} data.status - 状态（1-启用，0-禁用）
 * @returns {Promise} - 请求Promise
 */
export function updateDept(data) {
  return request({
    url: '/department/update',
    method: 'post',
    data
  });
}

/**
 * 删除科室
 * @param {number} id - 科室ID
 * @returns {Promise} - 请求Promise
 */
export function deleteDept(id) {
  return request({
    url: `/department/softDelete/${id}`, 
    method: 'delete',
    
  });
}