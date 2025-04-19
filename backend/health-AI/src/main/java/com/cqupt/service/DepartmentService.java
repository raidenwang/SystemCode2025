package com.cqupt.service;


import com.cqupt.domain.dto.Departments;
import com.cqupt.domain.po.Department;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface DepartmentService {
    List<Departments> getDepartmentsByHospitalId(int hospitalId);

    Department getDepartmentByHospitalId(int hospitalId, int departmentId);

    Integer addDepartment(String parentId,String departmentName,String info);

    String updateCategoryOrDepartment(String parentId, String name,String oldName,String info);

    String getDeptName(Integer deptId);

    Integer deleteDepartment(String name);

    Integer addHospitalDepartment(int i, Integer departmentId, String info);

    Integer getDeptId(String deptName);

    List<Departments> getDepartments();

    Department getDepartmentByDeptId(int departmentId);
}
