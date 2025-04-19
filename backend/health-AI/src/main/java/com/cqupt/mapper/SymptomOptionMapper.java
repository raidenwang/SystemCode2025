package com.cqupt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqupt.domain.po.SymptomOption;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SymptomOptionMapper  {

    void deleteByPartId(@Param("partId") long id);

    int getId();

    void insert(SymptomOption symptomOption);

    @MapKey("optionName")
    Map<String, Map<String, String>> getGroupedSymptoms(@Param("partID") long id);

    void batchInsert(@Param("list") List<SymptomOption> items);
}
