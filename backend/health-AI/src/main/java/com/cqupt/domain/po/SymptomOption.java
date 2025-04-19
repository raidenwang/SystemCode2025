package com.cqupt.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class SymptomOption {
    private long id;
    private long partId;
    private String optionName;
    private String symptoms;
}
