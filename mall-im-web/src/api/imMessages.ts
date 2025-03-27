import request from '@/utils/request';

export const temporaryChat = () => {
    return request.get(`/im-messages/temporary-chat`);
};

export const singleChat = (params: any) => {
    return request.get(`/im-messages/single-chat`, {params});
};
