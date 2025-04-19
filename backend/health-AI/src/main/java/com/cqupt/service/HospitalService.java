package com.cqupt.service;


import com.cqupt.domain.dto.HospitalDTO;
import com.cqupt.domain.po.Hospital;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface HospitalService {
    /**
     * 获取附近的三个医院
     * * */
    List<HospitalDTO> findNearbyHospitals(double lng, double lat);

    List<Hospital> getHospitalbyId(int id);
}
