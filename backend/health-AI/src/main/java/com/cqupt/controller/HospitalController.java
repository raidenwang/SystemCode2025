package com.cqupt.controller;



import com.cqupt.common.core.domain.AjaxResult;
import com.cqupt.domain.dto.HospitalDTO;
import com.cqupt.domain.po.Hospital;
import com.cqupt.service.HospitalService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/hospital")
@Api(tags = "医院相关接口")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;


    @PostMapping("/nearby")
    public ResponseEntity<List<HospitalDTO>> getNearbyHospitals(@RequestParam(defaultValue = "0") double latitude,
                                                                @RequestParam(defaultValue = "0") double longitude){
        List<HospitalDTO> nearbyHospitals = hospitalService.findNearbyHospitals(latitude,longitude);
        return ResponseEntity.ok(nearbyHospitals);
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id获取医院信息")
    public AjaxResult getHospitalById(@PathVariable int id){
        List<Hospital> hospitalInfo = hospitalService.getHospitalbyId(id);
        return AjaxResult.success(hospitalInfo);
    }

}
