package com.cqupt.controller;

import com.cqupt.common.core.domain.AjaxResult;
import com.cqupt.service.SymptomConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/symptoms")
@Api(tags="症状相关信息")
public class DiseaseController {
    @Autowired
    private  SymptomConfigService symptomConfigService;

    @GetMapping("/{partKey}")
    @ApiOperation("获取症状库")
    public AjaxResult getConfig(@PathVariable String partKey) {
        System.out.println("partKey"+partKey);
        Map<String,Object>config = symptomConfigService.getSymptomConfig(partKey);
        System.out.println("config is"+config);
            if (config==null)
            {
                return AjaxResult.error("部位不存在");
            }
            return AjaxResult.success(config);



    }

    @PostMapping("/{partKey}")
    public AjaxResult addConfig(@PathVariable String partKey,@RequestBody Map<String, List<String>> config) {
        try {
            symptomConfigService.updateSymptomConfig(partKey,config);
            return AjaxResult.success();
        }catch (Exception e){
            return AjaxResult.error("配置更新失败");
        }

    }

}
