import request from '@/utils/request';

export const listAll = () => {
    return request.get('/system/school/list-all');
};


export const index = () => {
    return request.get('/system/school/index');
};