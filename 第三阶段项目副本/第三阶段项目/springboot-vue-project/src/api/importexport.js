
import request from '@/utils/request' 

/**
 * 用户导入导出API
 */

/**
 * CSV导入用户数据
 * @param {File} file - CSV文件
 * @returns {Promise} 导入结果
 */
export function userImport(file) {
  const formData = new FormData()
  formData.append('multipartFile', file)
  
  return request({
    url: '/user/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 导出用户数据
 * @param {Object} params - 查询条件
 * @param {string} params.userName - 姓名（可选）
 * @param {string} params.userPhone - 手机号（可选）
 * @param {string} params.userCard - 身份证号（可选）
 * @param {string} params.userGender - 性别（可选）
 * @returns {Promise} 导出结果
 */
export function userExport(params) {
  return request({
    url: '/user/export',
    method: 'get',
    params: params,
    responseType: 'blob' // 重要：处理文件下载
  })
}

/**
 * 下载导入模板
 * @returns {Promise} 模板下载
 */
export function downloadTemplate() {
  return request({
    url: '/user/downloadTemplate',
    method: 'get',
    responseType: 'blob' // 重要：处理文件下载
  })
}