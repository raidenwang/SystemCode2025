package com.cqupt.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorAvailability {
    private long id;
    private String doctorId;
    private String day;
    private String time;
}
