package com.cqupt.service;


import com.cqupt.domain.dto.DoctorDTO;
import com.cqupt.domain.dto.DoctorDelDTO;
import com.cqupt.domain.po.DoctorAvailability;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface DoctorService {
    List<DoctorDTO> getDoctors(int hospitalId, int departmentId);

    List<DoctorAvailability> getDoctorAvailability(int doctorId);

    Integer addDoctor(String name, String title, String info, Integer deptId, Integer hospitalId);

    Integer updateDoctor(String oldName, String name, String title, String info, Integer deptId, Integer hospitalId);

    Integer updateDoctorTwo(String oldName, String name, String title, String info, Integer oldDeptId, Integer newDeptId, Integer hospitalId);

    int delDoctors(List<DoctorDelDTO> delData);
}
