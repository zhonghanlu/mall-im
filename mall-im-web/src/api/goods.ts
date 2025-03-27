import request from '@/utils/request';

export const getGoodsDetail = (id: string) => {
    return request.get(`/system/goods/${id}`);
};

export const getGoodsList = (params: any) => {
    return request.get('/system/goods/pc-list', {params});
};

export const recommended = () => {
    return request.get('/system/goods/recommended');
};

export const searchGoods = (keyword: string) => {
    return request.get('/goods/search', {params: {keyword}});
};

export const addView = (data: any) => {
    return request.post('/system/goods/add-view', data);
};
