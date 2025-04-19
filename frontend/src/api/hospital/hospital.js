import request from '@/utils/request.js';



// export function addDepartment(parentId,name,info) {
//     return request({
//         url: '/hospital/addDepartment?parentId=' + parentId +'&departmentName='+ name+'&info='+info,
//         method: 'post'
//     })
// }
export function addDepartment(addData){
    return request({
        url:'/hospital/addDepartment',
        method:'post',
        data:addData
    })
}
// export function updateCategoryOrDepartment(parentId,name,oldName,info){
//     return request({
//         url:'/hospital/updateCategoryOrDepartment?parentId='+parentId +'&Name='+ name+'&oldName='+oldName+'&info='+info,
//         method:'post'
//     })
// }
export function updateCategoryOrDepartment(updateData){
    return request({
        url:'/hospital/updateCategoryOrDepartment',
        method:'post',
        data:updateData
    })
}
export function getDeptName(deptId){
    return request({
        url:'/hospital/getDeptName?deptId='+deptId,
        method:'post'
    })
}

export function delDepartment(name){
    return request({
        url:'/hospital/deleteDepartment?name='+name,
        method:'post'
    })
}

