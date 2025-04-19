package com.cqupt.service.impl;

import com.cqupt.domain.po.DiseasePart;
import com.cqupt.domain.po.SymptomOption;
import com.cqupt.mapper.DiseaseMapper;
import com.cqupt.mapper.SymptomOptionMapper;
import com.cqupt.service.SymptomConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SymptomConfigServiceImpl implements SymptomConfigService {
    @Autowired
    private DiseaseMapper diseaseMapper;
    @Autowired
    private SymptomOptionMapper symptomOptionMapper;


    @Override
    public Map<String, Object> getSymptomConfig(String partKey) {
        DiseasePart diseasePart = diseaseMapper.selectByPartKey(partKey);
        if(diseasePart==null){
            return null;
        }
        //System.out.println("diseasePart:"+diseasePart);
        Map<String, Map<String, String>> groupedSymptoms = symptomOptionMapper.getGroupedSymptoms(diseasePart.getId());
        //System.out.println("symptomOptions="+groupedSymptoms);
        Map<String, List<String>> availableSymptomOptions = new LinkedHashMap<>();
        groupedSymptoms.forEach((optionName, map) -> {
            String symptomsStr = map.get("symptoms");
            availableSymptomOptions.put(optionName, Arrays.asList(symptomsStr.split(",")));
        });
        //System.out.println("available:"+availableSymptomOptions);
        Map<String,Object> result = new HashMap<>();
        result.put("diseasePart",diseasePart.getPartName());
        result.put("availableOptions",availableSymptomOptions);
        System.out.println("result:"+result);
        return result;
    }

    @Override
    public void updateSymptomConfig(String partKey, Map<String, List<String>> config) {
        DiseasePart diseasePart = diseaseMapper.selectByPartKey(partKey);
        int id = diseaseMapper.getId();
        if(diseasePart == null){
            diseasePart.setId(id);
            diseasePart.setPartKey(partKey);
            diseasePart.setPartName("新部位");
            diseaseMapper.insert(diseasePart);
        }

        symptomOptionMapper.deleteByPartId(diseasePart.getId());

        int symptomId = symptomOptionMapper.getId();
        List<SymptomOption> items = new ArrayList<>();
        config.forEach((optionName, symptoms) -> {
            symptoms.forEach(symptom -> {
                items.add(new SymptomOption(symptomId, diseasePart.getId(), optionName, symptom));
            });
        });

        // 4. 批量插入新配置
        if (!items.isEmpty()) {
            symptomOptionMapper.batchInsert(items);
        }
    }
}
