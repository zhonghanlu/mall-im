import request from '@/utils/request';

export const addCommunity = (data: any) => {
    return request.post(`/mall-community/add`, data);
};

export const listCommunity = (params: any) => {
    return request.get(`/mall-community/list`, {params});
};
