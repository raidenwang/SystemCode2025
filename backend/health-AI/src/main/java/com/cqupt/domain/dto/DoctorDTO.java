package com.cqupt.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDTO {
    private Integer id;
    private String name;
    private String title;
    private String info;
    private String imageUrl;
    private Integer departmentId;
    private Integer hospitalId;
    private String oldName;
    private Integer oldDepartmentId;
}
