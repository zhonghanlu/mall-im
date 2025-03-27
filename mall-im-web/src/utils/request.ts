import axios from 'axios';
import { ElMessage } from 'element-plus'

const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASEURL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
});

// 请求拦截器
service.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, error => {
  return Promise.reject(error);
});

// 响应拦截器
service.interceptors.response.use(
  response => {
    if (response.data.code !== 200) {
      ElMessage.error(response.data.msg || '请求异常');
      return Promise.reject(response.data);
    }
    return response.data;
  },
  error => {
    ElMessage.error(error.response?.data?.message || '服务异常');
    return Promise.reject(error);
  }
);

export default service
