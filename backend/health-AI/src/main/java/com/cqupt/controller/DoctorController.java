package com.cqupt.controller;

import com.cqupt.common.core.domain.AjaxResult;

import com.cqupt.domain.dto.DoctorAddDTO;
import com.cqupt.domain.dto.DoctorDTO;
import com.cqupt.domain.dto.DoctorDelDTO;
import com.cqupt.domain.dto.DoctorUpdateDTO;
import com.cqupt.domain.po.DoctorAvailability;
import com.cqupt.service.DepartmentService;
import com.cqupt.service.DoctorService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/hospital")
@Api(tags="医生相关信息")
public class DoctorController {
    @Autowired
    private DoctorService doctorService;

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/{hospitalId}/department/{departmentId}/doctors")
    @ApiOperation("根据医院id和部门id获取当前部门所有医生信息")
    public AjaxResult getDoctors(@PathVariable int hospitalId, @PathVariable int departmentId) {
        List<DoctorDTO> doctorDTOS = doctorService.getDoctors(hospitalId,departmentId);
        if (doctorDTOS != null && !doctorDTOS.isEmpty()) {
            return AjaxResult.success(doctorDTOS);
        } else {
            return AjaxResult.error("No doctors found for the given hospital ID and department ID");
        }
    }

    @GetMapping("/doctor/{doctorId}/availability")
    @ApiOperation("根据医生id获取医生排班表")
    public AjaxResult getDoctorAvailability(@PathVariable int doctorId) {
        List<DoctorAvailability> doctorAvailabilities=doctorService.getDoctorAvailability(doctorId);
        if (doctorAvailabilities != null && !doctorAvailabilities.isEmpty()) {
            return AjaxResult.success(doctorAvailabilities);
        }else{
            return AjaxResult.error("No doctor availability found for the given doctor ID");
        }
    }

    @PostMapping("/doctor/add")
    @ApiOperation("新增医生")
    public AjaxResult addDoctor(@RequestBody DoctorAddDTO addDTO) {
        String name = addDTO.getName();
        String deptName = addDTO.getDeptName();
        String title = addDTO.getTitle();
        String info = addDTO.getInfo();
        addDTO.setHospitalId(1);
        Integer hospitalId = addDTO.getHospitalId();
        System.out.println("name"+name);
        Integer deptId = departmentService.getDeptId(deptName);
        Integer doctorId = doctorService.addDoctor(name,title,info,deptId,hospitalId);
        if (doctorId !=null){
            return AjaxResult.success("200");
        }
        return AjaxResult.success();
    }

    @PostMapping("/doctor/updateDoctor")
    @ApiOperation("更新医生信息")
    public AjaxResult updateDoctor(@RequestBody DoctorUpdateDTO doctorUpdateDTO) {
        String oldDeptName = doctorUpdateDTO.getOldDeptName();
        System.out.println("oldDeptName"+oldDeptName);
        Integer oldDeptId = departmentService.getDeptId(oldDeptName);
        System.out.println("oldDeptId"+oldDeptId);
        Integer code;
        String deptName = doctorUpdateDTO.getDeptName();
        String oldName = doctorUpdateDTO.getOldName();
        String name = doctorUpdateDTO.getName();
        String title = doctorUpdateDTO.getTitle();
        String info = doctorUpdateDTO.getInfo();
        doctorUpdateDTO.setHospitalId(1);
        Integer hospitalId = doctorUpdateDTO.getHospitalId();
        if (deptName==null)
        {
            code = doctorService.updateDoctor(oldName, name, title, info, oldDeptId, hospitalId);
        }else {
            Integer newDeptId = departmentService.getDeptId(deptName);
            code = doctorService.updateDoctorTwo(oldName,name,title,info,oldDeptId,newDeptId,1);
        }
        if(code==200)
        {
            return AjaxResult.success("200");
        }
        return AjaxResult.warn("warn");

    }

    @PostMapping("/doctor/deleteDoctor")
    @ApiOperation("删除医生")
    public AjaxResult deleteDoctor(@RequestBody List<DoctorDelDTO> delData){
        System.out.println(delData);
        for(DoctorDelDTO delDTO:delData){
            System.out.println(departmentService.getDeptId(delDTO.getDeptName()));
            delDTO.setDeptId(departmentService.getDeptId(delDTO.getDeptName()));
        }
        int result = doctorService.delDoctors(delData);
        if (result==200){
            return AjaxResult.success();
        }else{
            return  AjaxResult.warn("something wrong");
        }
    }
}
