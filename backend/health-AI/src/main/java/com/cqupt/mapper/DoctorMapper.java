package com.cqupt.mapper;


import com.cqupt.domain.dto.DoctorDTO;
import com.cqupt.domain.po.DoctorAvailability;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DoctorMapper {
    List<DoctorDTO> getDoctors(@Param("hospitalId") int hospitalId, @Param("departmentId") int departmentId);

    List<DoctorAvailability> getDoctorAvailability(@Param("doctorId") int doctorId);

    void addDoctor(DoctorDTO doctorDTO);

    void updateDoctor( DoctorDTO doctorDTO);

    void updateDoctorTWO(DoctorDTO doctorDTO);

    void delDoctor(@Param("name") String name, @Param("deptId") Integer deptId);
}
