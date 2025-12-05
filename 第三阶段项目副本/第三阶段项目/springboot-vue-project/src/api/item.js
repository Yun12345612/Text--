import request from '@/utils/request' // 引入封装好的Axios实例

// 1. 新增体检项目
export function addItem(data) {
  return request({
    url: '/examination/item/add', 
    method: 'post',
    data
  });
}

// 2. 更新体检项目
export function updateItem(data) {
  return request({
    url: '/examination/item/update',
    method: 'post',
    data
  });
}

// 3. 软删除体检项目
export function softDeleteItem(itemId) {
  return request({
    url: `/examination/item/delete/${itemId}`,
    method: 'delete'
  });
}

// 4. 根据ID查询体检项目
export function getItemById(itemId) {
  return request({
    url: `/examination/item/${itemId}`,
    method: 'get'
  });
}

// 5. 分页多条件查询体检项目
export function getItemList(params) {
  return request({
    url: '/examination/item/list',
    method: 'get',
    params // 传递查询参数（如itemName、departmentId、价格区间、分页参数等）
  });
}
//6.动态获取所有科室列表
export function getDepartmentList() {
  return request({
    url: '/department/gteList',
    method: 'get'
  });
}
//7.查询所有项目明细
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