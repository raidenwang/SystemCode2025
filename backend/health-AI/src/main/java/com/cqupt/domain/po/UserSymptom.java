package com.cqupt.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSymptom {
    private  long recordId;
    private  long userId;
    private  String symptoms;
    private LocalDateTime createdAt;
}
