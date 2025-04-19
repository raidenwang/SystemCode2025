package com.cqupt.mapper;


import com.cqupt.domain.dto.DepartmentDTO;
import com.cqupt.domain.dto.Departments;
import com.cqupt.domain.po.Department;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DepartmentMapper {
    List<Departments> getDepartmentsByHospitalId(int hospitalId);

    Department getDepartmentByHospitalId(@Param("hospitalId") int hospitalId, @Param("departmentId") int departmentId);

    Integer addDepartment(DepartmentDTO departmentDTO);

    String getAncestors(@Param("parentId") String parentId);

    String getcategoryByparentId(@Param("parentId") String parentId);

    void updateCategory(@Param("oldName") String oldName,@Param("name") String name,@Param("info") String info);

    void updateName(@Param("category")String category, @Param("name") String name, @Param("info") String info, @Param("oldName") String oldName);

    String getDeptName(@Param("deptId") Integer deptId);

    void deleteDepartment(@Param("name") String name);

    DepartmentDTO getDeptByName(@Param("name") String name);

    Integer getDeptIdByName(@Param("deptName") String deptName);

    List<Departments> getDepartments();

    Department getDepartmentByDeptId(@Param("deptId") int departmentId);
}
