export function getBalance(userId) {
  return request({
    url: '/balance', // 接口路径（后端 @GetMapping("/balance")）
    method: 'get',   // 请求类型
    params: { userId } // GET 参数（会自动拼为 ?userId=xxx）
  });
}