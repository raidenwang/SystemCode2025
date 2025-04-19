package com.cqupt.mapper;


import com.cqupt.domain.dto.HospitalDTO;
import com.cqupt.domain.po.Hospital;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HospitalMapper {

    List<HospitalDTO> selectNearbyHospitals(double latitude, double longitude);

    List<Hospital> getHospitalbyId(int id);
}
