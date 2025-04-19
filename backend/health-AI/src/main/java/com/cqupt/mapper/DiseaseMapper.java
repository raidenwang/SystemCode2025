package com.cqupt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqupt.domain.po.DiseasePart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DiseaseMapper {
    DiseasePart selectByPartKey(@Param("partKey") String partKey);

    int getId();

    void insert(DiseasePart diseasePart);
}
