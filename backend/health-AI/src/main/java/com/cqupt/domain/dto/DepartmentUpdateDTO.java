package com.cqupt.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentUpdateDTO {
    private String parentId;
    private String name;
    private String oldName;
    private String info;
}
