package com.cqupt.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentDTO {
    String id;
    String departmentName;
    String category;
    String info;
}
