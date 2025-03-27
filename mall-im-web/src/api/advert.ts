import request from '@/utils/request';

export const getAdvertList = (advertType: number) => {
    return request.get(`/system/advert/list?advertType=`+advertType);
};

