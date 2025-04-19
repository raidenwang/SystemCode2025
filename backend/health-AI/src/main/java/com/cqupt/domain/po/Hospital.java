package com.cqupt.domain.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hospital {
    private long id;
    private String name;
    private String address;
    private String phone;
    private String info;
}

