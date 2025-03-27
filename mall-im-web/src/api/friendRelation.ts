import request from '@/utils/request';

export const addFriendRelation = (data: any) => {
    return request.post(`/im-relationships/add`, data);
};

export const friendApplyList = () => {
    return request.get(`/im-relationships/friend-apply-list`);
};

export const friendList = () => {
    return request.get(`/im-relationships/friend-list`);
};

export const optApply = (data: any) => {
    return request.post(`/im-relationships/opt-apply`, data);
};


