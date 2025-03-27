import request from '@/utils/request';

export const addOrder = (addOrder: any) => {
    return request.post(`/system/order`, addOrder);
};

export const orderList = (params: any) => {
    return request.get(`/system/order/own-list`, {params});
};
