import request from '@/utils/request';

export const getAddressList = () => {
    return request.get(`/system/address/list-all`);
};

export const editAddress = (data: any) => {
    return request.post(`/system/address/edit`, data);
};

export const addAddress = (data: any) => {
    return request.post(`/system/address/add`, data);
};

export const deleteAddress = (id: any) => {
    return request.get(`/system/address/delete/` + id);
};


