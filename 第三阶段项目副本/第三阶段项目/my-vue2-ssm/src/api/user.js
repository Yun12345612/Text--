import request from '@/utils/request' // 假设已配置 Axios 实例

/**
 * 用户/注册登录接口
 * @param {Object} params - 登录参数
 * @param {string} params.userAccount - 账号
 * @param {string} params.userPassword - 密码
 * @param {string} params.code - 验证码
 * @returns {Promise} - 登录结果Promise
 */
export function userLogin(params) {
  return request({
    url: '/user/login', // 对应后端 Controller 的 /user/login 接口
    method: 'post',
    data: params // POST 请求用 data 传递 JSON 数据
  })
}
export function userRegister(data) {
  return request({
    url: '/user/register', // 对应后端 Controller 的 /user/login 接口
    method: 'post',
    data: data // POST 请求用 data 传递 JSON 数据
  })
}


/**
 * 获取用户详情（扩展接口，可选）
 * @param {string} userId - 用户ID
 * @returns {Promise} - 用户详情Promise
 */
export function getUserInfo(userId) {
  return request({
    url: `/user/info/${userId}`,
    method: 'get'
  })
}
export function getCode() {
  return request({
    url: '/get/code',
    method: 'get',
    params: {
      timestamp: new Date().getTime() // 每次请求添加时间戳，避免浏览器缓存
    }
  });
}