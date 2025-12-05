import request from '@/utils/request'; 

/**
 * 1. 根据ID查询单个角色
 * 后端接口：@GetMapping("/{roleId}")
 * 示例路径：/role/1
 */
export function roleGetById(roleId) {
  return request({
    url: `/role/${roleId}`, 
    method: 'get' 
  });
}

/**
 * 2. 带条件查询角色列表（支持分页）
 * 后端接口：@GetMapping("/getrolelist")
 * 示例路径：/role/getrolelist?roleName=管理员&currentPage=1&pageSize=10
 * @param {Object} params - 包含分页参数（currentPage/pageSize）和查询条件（roleName/roleStatus等）
 */
export function roleGetList(params) {
  return request({
    url: '/role/getrolelist',
    method: 'get',
    params: params // GET请求参数，自动拼接到URL后
  });
}

/**
 * 3. 新增角色
 * 后端接口：@PostMapping("/addrolelist")
 * 示例路径：/role/addrolelist
 * @param {Object} data - 角色信息（roleName/roleStatus/roleDesc等，roleName和roleStatus为必填）
 */
export function roleAdd(data) {
  return request({
    url: '/role/addrolelist',
    method: 'post',
    data: data // POST请求体，JSON格式
  });
}

/**
 * 4. 修改角色
 * 后端接口：@PostMapping("/updaterolelist")
 * 示例路径：/role/updaterolelist
 * @param {Object} data - 角色信息（必须包含roleId，roleName和roleStatus为必填）
 */
export function roleUpdate(data) {
  return request({
    url: '/role/updaterolelist',
    method: 'post',
    data: data // POST请求体，JSON格式
  });
}

/**
 * 5. 软删除角色
 * 后端接口：@DeleteMapping("/softDelete/{roleId}")
 * 示例路径：/role/softDelete/1
 */
export function roleSoftDelete(roleId) {
  return request({
    url: `/role/softDelete/${roleId}`, 
    method: 'delete' 
  });
}