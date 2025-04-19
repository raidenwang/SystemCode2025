import request from '@/utils/request.js';



export function getHospitalById(hospitalId){
    return request({
        url:'/hospital/'+hospitalId,
        method:'get'
    })
}

export function getDepartmentsByHospitalId(hospitalId) {
    // 返回 request 发起的请求（Promise）
    return request({
      url: `/hospital/${hospitalId}/departments`,
      method: 'get'
    });
  }

export function getDepartmentById(departmentId){
    return request({
        url:`/hospital/1/department/${departmentId}`,
        method:'get'
    })
}
export function getDepartmentByDeptId(departmentId){
    return request({
        url:`/hospital/department/${departmentId}`,
        method:'get'
    })
}
export function getDoctors(departmentId){
    return request({
        url:`/hospital/1/department/${departmentId}/doctors`,
        method:'get'
    })
}

export function getDoctorAvailability(doctorId){
    return request({
        url:`/hospital/doctor/${doctorId}/availability`,
        method:'get'
    })
}

export function getDepartments(){
    return request({
        url:'/hospital/getDepartments',
        method:'get'
    })
}