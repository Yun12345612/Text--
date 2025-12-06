
//1.引入axios
import axios from 'axios';
//2.创建axios
var http = axios.create({
  baseURL: "/car-api", // 基础接口地址
  timeout: 50000 // 请求超时时间
});

//3.请求拦截器：添加 token
http.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token'); 
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    } 
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

//4.响应拦截器：统一处理响应
http.interceptors.response.use(
  response => {
    return response.data; // 直接返回响应体数据
  },
  error => {
    return Promise.reject(error);
  }
);

export default http; // 导出封装好的 axios 实例