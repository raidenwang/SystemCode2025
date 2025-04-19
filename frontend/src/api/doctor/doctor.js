import request from '@/utils/request.js';

export function addDoctor(addData){
    return request({
        url:'/hospital/doctor/add',
        method:'post',
        data:addData
    })
}

export function updateDoctor(updateData){
    return request({
        url:'/hospital/doctor/updateDoctor',
        method:'post',
        data:updateData
    })
}

export function deleteDoctors(delData){
    return request({
        url:'/hospital/doctor/deleteDoctor',
        method:'post',
        data:delData
    })
}