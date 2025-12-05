// @/api/examination.js
import request from '@/utils/request'

// 获取体检结果列表
export function getExamResultList(params) {
  return request({
    url: '/exam/result/list',
    method: 'get',
    params
  })
}

// 保存体检小结
export function saveExamSummary(data) {
  return request({
    url: '/exam/result/summary',
    method: 'post',
    data
  })
}

// 保存体检总结
export function saveExamConclusion(data) {
  return request({
    url: '/exam/result/conclusion',
    method: 'post',
    data
  })
}

// 生成总报告
export function generateReport(recordId) {
  return request({
    url: '/exam/result/generate',
    method: 'post',
    data: { recordId }
  })
}

// 获取体检详情
export function getExamDetail(recordId) {
  return request({
    url: `/exam/result/detail/${recordId}`,
    method: 'get'
  })
}

// 更新报告状态
export function updateReportStatus(data) {
  return request({
    url: '/exam/result/status',
    method: 'put',
    data
  })
}

/**
 * 体检结果API
 */

// 根据订单号获取体检结果
export const getExaminationResultByOrderNo = (orderNo) => {
  return request({
    method: 'GET',
    url: `/examination/result/${orderNo}`
  })
}

// 获取体检细项列表
export const getExaminationItems = (orderNo) => {
  return request({
    method: 'GET',
    url: `/examination/items/${orderNo}`
  })
}

// 保存体检结果
export const saveExaminationResult = (resultData) => {
  return request({
    method: 'POST',
    url: '/examination/result/save',
    data: resultData
  })
}

// 保存体检小结
export const saveExaminationSummary = (summaryData) => {
  return request({
    method: 'POST',
    url: '/examination/summary/save',
    data: summaryData
  })
}

// 获取体检小结
export const getExaminationSummary = (orderNo) => {
  return request({
    method: 'GET',
    url: `/examination/summary/${orderNo}`
  })
}
