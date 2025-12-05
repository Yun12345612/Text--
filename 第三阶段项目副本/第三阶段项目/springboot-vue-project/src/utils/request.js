import axios from "axios";
import { Message } from 'element-ui'; // 需要导入Message

const api = axios.create({
  baseURL: '/api',
  timeout: 5000,
  withCredentials: true
});

// 添加请求拦截器
api.interceptors.request.use(
  function (config) {
    // 确保每次请求都携带凭证
    config.withCredentials = true;

    // 获取token
    let token = localStorage.getItem("token");

    // 如果token存在,设置放在请求头里
    if (token) {
      config.headers['Authorization'] = token;
      console.log("设置Authorization:", token);
    }

    // 添加调试信息
    console.log('发送请求:', config.url, '携带Cookie:', config.withCredentials);

    // 在发送请求之前做些什么
    return config;
  },
  function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
  }
);

// 添加响应拦截器（只有一个）
api.interceptors.response.use(
  function (response) {
    const res = response.data;

    // 特殊处理：如果是文件上传/download请求，直接返回原始响应
    const url = response.config.url || '';
    if (url.includes('/import') || url.includes('/download') ||
      response.config.responseType === 'blob') {
      return response; // 返回完整的response对象
    }

    // 处理双重data嵌套
    if (res && typeof res === 'object' && 'code' in res) {
      if (res.code === 200) {
        return res.data;  // 直接返回业务数据
      } else {
        Message.error(res.msg || '请求失败');
        return Promise.reject(new Error(res.msg || 'Error'));
      }
    }

    // 如果没有code字段，直接返回
    return res;
  },
  function (error) {
    // 超出 2xx 范围的状态码都会触发该函数。
    console.error('请求错误:', error);

    if (error.response) {
      // 服务器返回错误状态码
      switch (error.response.status) {
        case 401:
          Message.error('未授权，请重新登录');
          // 可以跳转到登录页
          break;
        case 403:
          Message.error('拒绝访问');
          break;
        case 404:
          Message.error('请求地址不存在');
          break;
        case 500:
          Message.error('服务器内部错误');
          break;
        default:
          Message.error('网络异常，请稍后重试');
      }
    } else if (error.request) {
      // 请求发送了但没有收到响应
      Message.error('网络连接失败，请检查网络');
    } else {
      // 其他错误
      Message.error('请求配置错误');
    }

    return Promise.reject(error);
  }
);

export default api;