package com.cqupt.service.impl;


import com.cqupt.domain.dto.DoctorDTO;
import com.cqupt.domain.dto.DoctorDelDTO;
import com.cqupt.domain.po.DoctorAvailability;
import com.cqupt.mapper.DoctorMapper;
import com.cqupt.service.DoctorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DoctorImpl implements DoctorService {
    @Autowired
    private DoctorMapper doctorMapper;

    @Override
    public List<DoctorDTO> getDoctors(int hospitalId, int departmentId) {
        return doctorMapper.getDoctors(hospitalId,departmentId);
    }

    @Override
    public List<DoctorAvailability> getDoctorAvailability(int doctorId) {
        return doctorMapper.getDoctorAvailability(doctorId);
    }

    @Override
    public Integer addDoctor(String name, String title, String info, Integer deptId, Integer hospitalId) {
        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setName(name);
        doctorDTO.setTitle(title);
        doctorDTO.setInfo(info);
        doctorDTO.setImageUrl("https://medical-chat.oss-cn-beijing.aliyuncs.com/doctor_default.jpg");
        doctorDTO.setDepartmentId(deptId);
        doctorDTO.setHospitalId(hospitalId);
        doctorMapper.addDoctor(doctorDTO);
        return doctorDTO.getId();
    }

    @Override
    public Integer updateDoctor(String oldName,String name, String title, String info, Integer deptId, Integer hospitalId) {
        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setName(name);
        doctorDTO.setTitle(title);
        doctorDTO.setInfo(info);
        doctorDTO.setDepartmentId(deptId);
        doctorDTO.setHospitalId(hospitalId);
        doctorDTO.setOldName(oldName);
        doctorMapper.updateDoctor(doctorDTO);
        return 200;
    }

    @Override
    public Integer updateDoctorTwo(String oldName, String name, String title, String info, Integer oldDeptId, Integer newDeptId, Integer hospitalId) {
        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setName(name);
        doctorDTO.setTitle(title);
        doctorDTO.setInfo(info);
        doctorDTO.setDepartmentId(newDeptId);
        doctorDTO.setOldDepartmentId(oldDeptId);
        doctorDTO.setOldName(oldName);
        doctorDTO.setHospitalId(hospitalId);
        doctorMapper.updateDoctorTWO(doctorDTO);
        return 200;
    }

    @Override
    public int delDoctors(List<DoctorDelDTO> delData) {
        for(DoctorDelDTO delDTO:delData){
            doctorMapper.delDoctor(delDTO.getName(),delDTO.getDeptId());
        }
        return 200;
    }
}
