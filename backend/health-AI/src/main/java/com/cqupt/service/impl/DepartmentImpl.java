package com.cqupt.service.impl;


import com.cqupt.domain.dto.DepartmentDTO;
import com.cqupt.domain.dto.Departments;
import com.cqupt.domain.po.Department;
import com.cqupt.mapper.DepartmentMapper;
import com.cqupt.service.DepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DepartmentImpl implements DepartmentService {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Override
    public List<Departments> getDepartmentsByHospitalId(int hospitalId) {
        System.out.println("hospital id is " + hospitalId);
        return departmentMapper.getDepartmentsByHospitalId(hospitalId);
    }

    @Override
    public Department getDepartmentByHospitalId(int hospitalId, int departmentId) {
        return departmentMapper.getDepartmentByHospitalId(hospitalId,departmentId);
    }

    @Override
    public Integer addDepartment(String parentId,String departmentName,String info) {
        String ancestors = departmentMapper.getAncestors(parentId);
        System.out.println("ancestors is " + ancestors);
        if (ancestors.indexOf(',')!=-1&& ancestors.indexOf(',')==ancestors.lastIndexOf(',')) {
            DepartmentDTO dto = new DepartmentDTO();
            dto.setDepartmentName(departmentName);
            System.out.println("departmentName is " + dto.getDepartmentName());
            String category = departmentMapper.getcategoryByparentId(parentId);
            dto.setCategory(category);
            System.out.println("category is " + dto.getCategory());
            dto.setInfo(info);
            System.out.println("info is " + dto.getInfo());
            return departmentMapper.addDepartment(dto);
        }
        System.out.println("fail");
        return -1;
    }

    @Override
    public String updateCategoryOrDepartment(String parentId, String name,String oldName,String info) {
        String ancestors = departmentMapper.getAncestors(parentId);
        System.out.println("ancestors is " + ancestors);
        if (ancestors.indexOf(',')==-1){
            System.out.println("oldName is " + oldName);
            System.out.println("name is " + name);
            departmentMapper.updateCategory(oldName,name,info);
            return "category  update success";

        } else if (ancestors.indexOf(',')!=-1&& ancestors.indexOf(',')==ancestors.lastIndexOf(',')) {
            System.out.println("oldName is "+oldName);
            String category = departmentMapper.getcategoryByparentId(parentId);
            departmentMapper.updateName(category,name,info,oldName);
            return "name update success";
        }

        return "something wrong";
    }

    @Override
    public String getDeptName(Integer deptId) {
        return departmentMapper.getDeptName(deptId);
    }

    @Override
    public Integer deleteDepartment(String deptName) {
        new DepartmentDTO();
        DepartmentDTO departmentDTO;
        departmentDTO = departmentMapper.getDeptByName(deptName);
        if(departmentDTO==null){
            return 300;
        }else{
            departmentMapper.deleteDepartment(deptName);
            return 200;
        }
    }

    @Override
    public Integer addHospitalDepartment(int i, Integer departmentId, String info) {


        return departmentId;
    }

    @Override
    public Integer getDeptId(String deptName) {
        return departmentMapper.getDeptIdByName(deptName);
    }

    @Override
    public List<Departments> getDepartments() {
        return departmentMapper.getDepartments();
    }

    @Override
    public Department getDepartmentByDeptId(int departmentId) {
        return departmentMapper.getDepartmentByDeptId(departmentId);
    }


}
