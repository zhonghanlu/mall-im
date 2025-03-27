import request from '@/utils/request';

type LoginParams = {
    username: string;
    password: string;
};

type RegisterParams = {
    nickName: string,
    username: string,
    password: string,
    schoolId: string
};

export const login = (data: LoginParams) => {
    return request.post('/login', data);
};

export const getInfo = () => {
    return request.get('/getInfo');
};

export const logout = () => {
    return request.post('/logout');
};

export const register = (data: RegisterParams) => {
    return request.post('/register', data);
};

export const updateUser = (params: any) => {
    return request.put('/system/user/profile', params);
};
