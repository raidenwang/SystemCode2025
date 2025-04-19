package com.cqupt.service;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface SymptomConfigService {
    Map<String, Object> getSymptomConfig(String partKey);

    void updateSymptomConfig(String partKey, Map<String, List<String>> config);
}
