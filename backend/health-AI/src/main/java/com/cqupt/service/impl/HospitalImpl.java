package com.cqupt.service.impl;


import com.cqupt.domain.dto.HospitalDTO;
import com.cqupt.domain.po.Hospital;
import com.cqupt.mapper.HospitalMapper;
import com.cqupt.service.HospitalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class HospitalImpl implements HospitalService {
    @Autowired
    private HospitalMapper hospitalMapper;


    public List<HospitalDTO> findNearbyHospitals(double latitude, double longitude) {
        return hospitalMapper.selectNearbyHospitals(latitude, longitude);
    }

    @Override
    public List<Hospital> getHospitalbyId(int id) {
        return hospitalMapper.getHospitalbyId(id);
    }

}
