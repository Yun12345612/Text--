// 引入封装好的request实例
import request from '@/utils/request'


// 1. 卡入库接口（对应后端 /card/import）
export const importCard = (data) => {
  return request({
    method: 'POST',
    url: '/card/import', 
    data: data
  });
};

// 2. 卡片列表查询接口（对应后端 /card/list）
export const getCardList = (searchParams) => {
  return request({
    method: 'GET',
    url: '/card/list',
    params: searchParams // GET请求用params传参（包含查询条件+分页：cardNoStart、cardStatus、pageNum、pageSize等）
  })
}

// 3. 卡片删除接口（对应后端 /card/delete）
export const deleteCard = (cardNo) => {
  return request({
    method: 'POST',
    url: '/card/delete',
    data: { cardNo: cardNo } // 传要删除的卡号
  })
}