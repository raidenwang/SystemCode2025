package com.cqupt.controller;



import com.cqupt.common.core.domain.AjaxResult;
import com.cqupt.domain.dto.DepartmentAddDTO;
import com.cqupt.domain.dto.DepartmentUpdateDTO;
import com.cqupt.domain.dto.Departments;
import com.cqupt.domain.po.Department;
import com.cqupt.service.DepartmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/hospital")
@Api(tags="科室相关信息")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/{hospitalId}/departments")
    @ApiOperation("根据医院id获取所有部门信息")
    public AjaxResult getDepartmentsByHospitalId(@PathVariable int hospitalId) {
        List<Departments> departments = departmentService.getDepartmentsByHospitalId(hospitalId);
        System.out.println(departments);
        if (departments != null && !departments.isEmpty()) {
            return AjaxResult.success(departments);
        } else {
            return AjaxResult.error("No departments found for the given hospital ID");
        }
    }
    @GetMapping("/getDepartments")
    @ApiOperation("直接获取所有部门信息")
    public AjaxResult getDepartments(){
        List<Departments> departments = departmentService.getDepartments();
        System.out.println(departments);
        if(departments!=null && !departments.isEmpty()){
            return AjaxResult.success(departments);
        }else {
            return AjaxResult.error("No departments found ");
        }
    }

    @GetMapping("/{hospitalId}/department/{departmentId}")
    @ApiOperation("根据医院id和部门id获取当前部门信息")
    public AjaxResult getDepartmentById(@PathVariable int hospitalId, @PathVariable int departmentId) {
        Department department = departmentService.getDepartmentByHospitalId(hospitalId,departmentId);
        if (department != null) {
            return AjaxResult.success(department);
        }else{
            return AjaxResult.error("No department found for the given hospital ID and department ID");
        }
    }
    @GetMapping("/department/{departmentId}")
    @ApiOperation("根据部门id获取当前信息")
    public AjaxResult getDepartmentByDeptId(@PathVariable int departmentId){
        Department department = departmentService.getDepartmentByDeptId(departmentId);
        if (department!=null){
            return AjaxResult.success(department);
        }else {
            return  AjaxResult.error("No department found for the given department ID");
        }
    }

    @PostMapping("/addDepartment")
    @ApiOperation("新增部门")
    public AjaxResult addDepartment(@RequestBody DepartmentAddDTO addData) {
        String parentId = addData.getParentId();
        String departmentName = addData.getDeptName();
        String info = addData.getInfo();
        Integer departmentId = departmentService.addDepartment(parentId,departmentName,info);

        if(departmentId!=-1)
        {
            return AjaxResult.success(departmentId);
        }else {
            return AjaxResult.success("this is not a department");
        }
    }

    @PostMapping("/updateCategoryOrDepartment")
    @ApiOperation("更新医院科室")
    public AjaxResult updateCategoryOrDepartment(@RequestBody DepartmentUpdateDTO updateDTO) {
        String parentId = updateDTO.getParentId();
        String Name = updateDTO.getName();
        String oldName = updateDTO.getOldName();
        String info = updateDTO.getInfo();
        String msg = departmentService.updateCategoryOrDepartment(parentId,Name,oldName,info);
        return AjaxResult.success(msg);
    }

    @PostMapping("/getDeptName")
    @ApiOperation("获取部门名字")
    public AjaxResult getDeptName(Integer deptId){
        System.out.println("deptId"+deptId);
        String deptName = departmentService.getDeptName(deptId);
        System.out.println("deptName"+deptName);
        return AjaxResult.success(deptName);
    }

    @PostMapping("/deleteDepartment")
    @ApiOperation("删除部门")
    public  AjaxResult deleteDepartment(String name){
        Integer flag = 0;
        flag = departmentService.deleteDepartment(name);
        if(flag==200)
        {
            return AjaxResult.success("third department del success");
        }
        return AjaxResult.success("this is not a third department");
    }



}
