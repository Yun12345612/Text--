
import request from '@/utils/request' // 引入封装好的Axios实例

//1.分页多条件查询体检项目
export function getItemList(params) {
  return request({
    url: '/examination/item/list',
    method: 'get',
    params // 传递查询参数（如itemName、departmentId、价格区间、分页参数等）
  });
}