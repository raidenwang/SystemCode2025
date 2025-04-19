package com.cqupt.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cqupt.common.core.domain.AjaxResult;
import com.cqupt.common.utils.SecurityUtils;
import com.cqupt.domain.constant.KnowledgeBaseConstants;
import com.cqupt.domain.dto.FileUploadDTO;
import com.cqupt.domain.dto.FileUploadTempDTO;
import com.cqupt.domain.dto.KnowledgeBaseDTO;
import com.cqupt.domain.po.FileEntity;
import com.cqupt.domain.po.KnowledgeBase;
import com.cqupt.domain.vo.FileStatusVO;
import com.cqupt.domain.vo.FileVO;
import com.cqupt.mapper.FileMapper;
import com.cqupt.service.KnowledgeBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/knowledgeBase")
@Api(tags = "知识库相关接口")
public class KnowledgeBaseController {

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    @Autowired
    private FileMapper fileMapper;

    @PostMapping("/add")
    @ApiOperation("新增知识库")
    public AjaxResult addKnowledgeBase(@RequestBody KnowledgeBaseDTO knowledgeBaseDTO) {

        Integer flag = knowledgeBaseService.addKnowledgeBase(knowledgeBaseDTO);
        if(flag == KnowledgeBaseConstants.KNOWLEDGE_BASE_CREATE_SUCCESS){
            return AjaxResult.success("添加知识库成功");
        }else if (flag == KnowledgeBaseConstants.KNOWLEDGE_BASE_CREATE_FAILURE) {
            return AjaxResult.error("添加知识库失败");
        }else if(flag == KnowledgeBaseConstants.KNOWLEDGE_BASE_NAME_OR_TYPE_IS_NULL){
            return AjaxResult.error("知识库名称或类型不能为空");
        }else if (flag == KnowledgeBaseConstants.KNOWLEDGE_BASE_IS_EXIST){
            return AjaxResult.error("知识库已存在");
        }else {
            return AjaxResult.error();
        }
    }

    @GetMapping("/list")
    @ApiOperation("查询当前用户的知识库列表")
    public AjaxResult list() {
        String username = SecurityUtils.getUsername();
        List<KnowledgeBase> kbList = knowledgeBaseService.list(username);
        return AjaxResult.success(kbList);
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation("删除知识库")
    public AjaxResult delete(@PathVariable Long id) {
        Integer flag = knowledgeBaseService.deleteById(id);
        if(flag == KnowledgeBaseConstants.KNOWLEDGE_BASE_DELETE_SUCCESS){
            return AjaxResult.success("删除知识库成功");
        }else if (flag == KnowledgeBaseConstants.KNOWLEDGE_BASE_DELETE_FAILURE) {
            return AjaxResult.error("删除知识库失败");
        }else if(flag == KnowledgeBaseConstants.KNOWLEDGE_BASE_NOT_EXIST){
            return AjaxResult.error("知识库不存在");
        }else {
            return AjaxResult.error();
        }
    }

    @PutMapping("/update")
    @ApiOperation("更新知识库")
    public AjaxResult update(@RequestBody KnowledgeBaseDTO knowledgeBaseDTO) {
        Integer flag = knowledgeBaseService.updateById(knowledgeBaseDTO);
        if(flag == KnowledgeBaseConstants.KNOWLEDGE_BASE_UPDATE_SUCCESS){
            return AjaxResult.success("更新知识库成功");
        }else if (flag == KnowledgeBaseConstants.KNOWLEDGE_BASE_NOT_EXIST) {
            return AjaxResult.error("更新知识库失败");
        }else{
            return AjaxResult.error();
        }
    }

    @PostMapping("/uploadFiles")
    @ApiOperation("上传文件")
    public AjaxResult uploadFiles(
            @RequestPart String fileList,
            @RequestParam Long knowledgeBaseId,
            @RequestParam MultipartFile[] files
    ) throws IOException {

        JSONArray jsonArray = JSONObject.parseArray(fileList);
        System.out.println("fileList"+fileList);
        List<FileUploadTempDTO> fileUploadTempDTOs = jsonArray.toJavaList(FileUploadTempDTO.class);
        List<FileUploadDTO> fileUploadDTOs = new ArrayList<>();
        for (FileUploadTempDTO fileUploadTempDTO : fileUploadTempDTOs) {
            FileUploadDTO fileUploadDTO = new FileUploadDTO();
            fileUploadDTO.setInstitution(fileUploadTempDTO.getInstitution());
            if (fileUploadTempDTO.getTag() != null) {
                fileUploadDTO.setTag(fileUploadTempDTO.getTag());
            }
            fileUploadDTO.setDate(LocalDate.parse(fileUploadTempDTO.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            for (MultipartFile file : files) {
                String filename = file.getOriginalFilename();
                System.out.println("filename"+filename);
                if(filename.equals(fileUploadTempDTO.getFilename())){
                    fileUploadDTO.setFile(file);
                    break;
                }
            }
            fileUploadDTOs.add(fileUploadDTO);
        }

        Integer flag = knowledgeBaseService.uploadFiles(knowledgeBaseId, fileUploadDTOs);
        System.out.println("flag"+flag);
        if(flag == KnowledgeBaseConstants.FILE_UPLOAD_SUCCESS){
            return AjaxResult.success("上传文件成功");
        }else if (flag == KnowledgeBaseConstants.FILE_UPLOAD_FAILURE) {
            return AjaxResult.error("上传文件失败");
        }else if (flag == KnowledgeBaseConstants.FILE_IS_EXIST) {
            return AjaxResult.error("请勿上传重复文件");
        }else{
            return AjaxResult.error();
        }
    }

    @GetMapping("/list_files/{knowledgeBaseId}")
    @ApiOperation("获取知识库文件列表")
    public AjaxResult listFiles(@PathVariable Long knowledgeBaseId) {
        List<FileEntity> files = fileMapper.listByKnowledgeBaseId(knowledgeBaseId);
        List<FileVO> fileList = new ArrayList<>();
        for (FileEntity file : files) {
            FileVO fileVO = new FileVO();
            BeanUtils.copyProperties(file, fileVO);
            JSONObject json = JSONObject.parseObject(file.getTag());
            fileVO.setTag(json);
            fileList.add(fileVO);
        }
        System.out.println("fileList"+fileList);
        return AjaxResult.success(fileList);
    }

    @DeleteMapping("/delete_file")
    @ApiOperation("删除文件")
    public AjaxResult deleteFile(@RequestParam Long knowledgeBaseId,
                                 @RequestParam Long[] fileIds) {
        Integer flag = knowledgeBaseService.deleteFiles(knowledgeBaseId, fileIds);
        if(flag == KnowledgeBaseConstants.FILES_DELETE_SUCCESS){
            return AjaxResult.success("删除文件成功");
        }else if (flag == KnowledgeBaseConstants.FILES_DELETE_FAILURE) {
            return AjaxResult.error("删除文件失败");
        }else{
            return AjaxResult.error();
        }
    }

    @GetMapping("/get_file_status/{knowledgeBaseId}")
    @ApiOperation("获取某一知识库下的文件状态")
    public AjaxResult getFileStatus(@PathVariable Long knowledgeBaseId) {
        List<FileStatusVO> fileStatusList = knowledgeBaseService.getFileStatus(knowledgeBaseId);
        return AjaxResult.success(fileStatusList);
    }

}
