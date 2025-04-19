package com.cqupt.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiseasePart {
    private long id;
    private String partKey;
    private String partName;
}
