import request from '@/utils/request.js';

export function getAllCategories(partKey){
    return request({
        url:`/symptoms/${partKey}`,
        method:'get'
    })
}