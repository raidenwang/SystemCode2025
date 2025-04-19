package com.cqupt.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorUpdateDTO {
    private String oldDeptName;
    private String oldName;
    private String name;
    private String title;
    private String info;
    private String deptName;
    private Integer hospitalId;
}
