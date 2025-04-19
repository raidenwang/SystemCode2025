package com.cqupt.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentAddDTO {
    private  String parentId;
    private  String deptName;
    private  String info;

}
