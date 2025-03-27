import request from '@/utils/request';

export const alipay = (orderId: any) => {
    return request.get(`/alipay/pay`, orderId);
};
