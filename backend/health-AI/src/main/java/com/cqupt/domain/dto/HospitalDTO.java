package com.cqupt.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HospitalDTO {
    private Long id;
    private String name;
    private double latitude;
    private double longitude;
}
