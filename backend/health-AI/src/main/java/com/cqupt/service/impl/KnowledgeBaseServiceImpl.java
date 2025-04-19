package com.cqupt.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cqupt.common.utils.SecurityUtils;
import com.cqupt.domain.constant.KnowledgeBaseConstants;
import com.cqupt.domain.dto.FileUploadDTO;
import com.cqupt.domain.dto.KnowledgeBaseDTO;
import com.cqupt.domain.po.FileEntity;
import com.cqupt.domain.po.KnowledgeBase;
import com.cqupt.domain.vo.FileStatusVO;
import com.cqupt.mapper.FileMapper;
import com.cqupt.mapper.KnowledgeBaseMapper;
import com.cqupt.service.KnowledgeBaseService;
import com.cqupt.utils.HttpUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {

    @Value(value = "${qanything.url}")
    private String qanythingUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private KnowledgeBaseMapper knowledgeBaseMapper;

    @Autowired
    private FileMapper fileMapper;

    @Override
    public Integer addKnowledgeBase(KnowledgeBaseDTO knowledgeBaseDTO) {
        if(knowledgeBaseDTO.getName().isEmpty() || knowledgeBaseDTO.getType() == null) {
            return KnowledgeBaseConstants.KNOWLEDGE_BASE_NAME_OR_TYPE_IS_NULL;
        }

        String userName = SecurityUtils.getUsername();
        KnowledgeBase kb = knowledgeBaseMapper.getByNameAndUserName(knowledgeBaseDTO.getName(), userName);

        if(kb != null) {
            return KnowledgeBaseConstants.KNOWLEDGE_BASE_IS_EXIST;
        }

        String url = qanythingUrl + "new_knowledge_base";

        JSONObject json = new JSONObject();
        json.put("kb_name", knowledgeBaseDTO.getName());
        json.put("user_id", "zzp"); //此处 user_id 需要满足： 以字母开头，只允许包含字母，数字或下划线。

        String res = null;
        try {
            res = HttpUtil.HttpRestClient(url, HttpMethod.POST, json);
        } catch (Exception e) {
            return KnowledgeBaseConstants.SERVER_ERROR;
        }

        JSONObject jsonObject = JSON.parseObject(res);
        Integer code = jsonObject.getInteger("code");
        if(code != 200) {
            return KnowledgeBaseConstants.KNOWLEDGE_BASE_CREATE_FAILURE;
        }

        String kbId = jsonObject.getJSONObject("data").getString("kb_id");

        KnowledgeBase knowledgeBase = new KnowledgeBase();
        knowledgeBase.setName(knowledgeBaseDTO.getName());
        knowledgeBase.setKbId(kbId);
        knowledgeBase.setCreator(userName);
        knowledgeBase.setType(knowledgeBaseDTO.getType());
        knowledgeBase.setCreateTime(new Date());
        knowledgeBase.setUpdateTime(new Date());
        knowledgeBase.setDeleted(false);

        knowledgeBaseMapper.add(knowledgeBase);

        return KnowledgeBaseConstants.KNOWLEDGE_BASE_CREATE_SUCCESS;
    }

    @Override
    public List<KnowledgeBase> list(String username) {
        List<KnowledgeBase> kbList =knowledgeBaseMapper.list(username);
        return kbList;
    }

    @Override
    @Transactional
    public Integer deleteById(Long id) {
        KnowledgeBase kb = knowledgeBaseMapper.getById(id);
        if(kb == null){
            return KnowledgeBaseConstants.KNOWLEDGE_BASE_NOT_EXIST;
        }

        String kbId = kb.getKbId();
//        String userName = kb.getCreator();

        String url = qanythingUrl + "delete_knowledge_base";

        JSONObject json = new JSONObject();
        json.put("user_id", "zzp");
        String[] kbIds = new String[]{kbId};
        json.put("kb_ids", kbIds);
        String res = null;
        try {
            res = HttpUtil.HttpRestClient(url, HttpMethod.POST, json);
        } catch (Exception e) {
            return KnowledgeBaseConstants.SERVER_ERROR;
        }

        JSONObject jsonObject = JSON.parseObject(res);
        Integer code = jsonObject.getInteger("code");
        if(code != 200) {
            return KnowledgeBaseConstants.KNOWLEDGE_BASE_DELETE_FAILURE;
        }

        knowledgeBaseMapper.deleteById(id);
        fileMapper.deleteByKnowledgeBaseId(id);

        return KnowledgeBaseConstants.KNOWLEDGE_BASE_DELETE_SUCCESS;
    }

    @Override
    public Integer updateById(KnowledgeBaseDTO knowledgeBaseDTO) {
        KnowledgeBase kb = knowledgeBaseMapper.getById(knowledgeBaseDTO.getId());
        if(kb == null){
            return KnowledgeBaseConstants.KNOWLEDGE_BASE_NOT_EXIST;
        }

        KnowledgeBase knowledgeBase = new KnowledgeBase();
        BeanUtils.copyProperties(knowledgeBaseDTO, knowledgeBase);
        knowledgeBase.setUpdateTime(new Date());

        knowledgeBaseMapper.updateById(knowledgeBase);
        return KnowledgeBaseConstants.KNOWLEDGE_BASE_UPDATE_SUCCESS;
    }

    @Override
    public Integer uploadFiles(Long knowledgeBaseId, List<FileUploadDTO> fileUploadDTOs) throws IOException {
        System.out.println("knowledgeBaseId = " + knowledgeBaseId);
        KnowledgeBase kb = knowledgeBaseMapper.getById(knowledgeBaseId);
        System.out.println("kb"+kb);
        String kbId = kb.getKbId();
        String userName = kb.getCreator();
        String url = qanythingUrl + "upload_files";

        List<MultipartFile> files = new ArrayList<>();
        List<FileEntity> fileEntities = new ArrayList<>();

        for (FileUploadDTO fileUploadDTO : fileUploadDTOs) {
            MultipartFile file = fileUploadDTO.getFile();
            files.add(file);
            String filename = file.getOriginalFilename();
            FileEntity fileEntity = new FileEntity();
            fileEntity.setName(filename);
            fileEntity.setInstitution(fileUploadDTO.getInstitution());
            fileEntity.setCreator(userName);
            fileEntity.setKnowledgeBaseId(knowledgeBaseId);
            fileEntity.setDate(fileUploadDTO.getDate());
            if (fileUploadDTO.getTag() != null) {
                fileEntity.setTag(fileUploadDTO.getTag());
            }
            fileEntities.add(fileEntity);
        }

        // 利用Set判断上传的文件中是否有重复文件
        if (fileEntities.size() > 1) {
            Set<FileEntity> fileSet = new HashSet<>();
            for (FileEntity file : fileEntities) {
                if (!fileSet.add(file)) {
                    return KnowledgeBaseConstants.FILE_IS_EXIST;
                }
            }
        }

        for (FileEntity fileEntity : fileEntities) {
            FileEntity f = fileMapper.getByCondition(fileEntity.getName(), fileEntity.getDate(), fileEntity.getInstitution(), userName);
            if (f != null) {
                return KnowledgeBaseConstants.FILE_IS_EXIST;
            }
        }

        // 构建 MultiValueMap，用于传递文件和表单数据
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        // 添加文件
        for (MultipartFile file : files) {
            builder.part("files", file.getResource());
        }
        System.out.println("kbID"+kbId);
        // 添加表单数据
        builder.part("user_id", "zzp");
        builder.part("kb_id", kbId);
        builder.part("mode", "strong");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // 创建请求实体
        HttpEntity<MultiValueMap<String, HttpEntity<?>>> requestEntity =
                new HttpEntity<>(builder.build(), headers);

        // 发送 POST 请求
        ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        JSONObject jsonObject = JSON.parseObject(res.getBody());
        Integer code = jsonObject.getInteger("code");
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        if(code != 200 || jsonArray.isEmpty()) {
            return KnowledgeBaseConstants.FILE_UPLOAD_FAILURE;
        }

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            String fileName = jsonObject1.getString("file_name");
            String fileId = jsonObject1.getString("file_id");
//            String status = jsonObject1.getString("status");

            for (FileEntity file : fileEntities) {
                if(fileName.equals(file.getName().replaceAll("[（）]", ""))){
                    file.setFileId(fileId);
//                    file.setStatus(status);
                    file.setDeleted(false);
                    file.setCreateTime(new Date());
                    file.setUpdateTime(new Date());
                    break;
                }
            }
        }
        fileMapper.addBatch(fileEntities);

        return KnowledgeBaseConstants.FILE_UPLOAD_SUCCESS;
    }

    @Override
    public Integer deleteFiles(Long knowledgeBaseId, Long[] fileIds) {
        KnowledgeBase kb = knowledgeBaseMapper.getById(knowledgeBaseId);
        String kbId = kb.getKbId();
//        String userName = kb.getCreator();
        String url = qanythingUrl + "delete_files";

        List<FileEntity> files = fileMapper.getByIds(fileIds);
        List<String> fileIdsList = files.stream().map(FileEntity::getFileId).collect(Collectors.toList());

        JSONObject json = new JSONObject();
        json.put("user_id", "zzp");
        json.put("kb_id", kbId);
        json.put("file_ids", fileIdsList);

        String res = null;
        try {
            res = HttpUtil.HttpRestClient(url, HttpMethod.POST, json);
        } catch (Exception e) {
            return KnowledgeBaseConstants.SERVER_ERROR;
        }

        JSONObject jsonObject = JSON.parseObject(res);
        Integer code = jsonObject.getInteger("code");
        if(code != 200) {
            return KnowledgeBaseConstants.FILES_DELETE_FAILURE;
        }

        fileMapper.deleteByIds(fileIds);

        return KnowledgeBaseConstants.FILES_DELETE_SUCCESS;
    }

    @Override
    public List<FileStatusVO> getFileStatus(Long knowledgeBaseId) {
        List<FileEntity> files = fileMapper.listByKnowledgeBaseId(knowledgeBaseId);
        List<FileStatusVO> fileStatusList = new ArrayList<>();
        KnowledgeBase kb = knowledgeBaseMapper.getById(knowledgeBaseId);

        String kbId = kb.getKbId();
//        String userName = kb.getCreator();
        String url = qanythingUrl + "list_files";

        JSONObject json = new JSONObject();
        json.put("user_id", "zzp");
        json.put("kb_id", kbId);
        String res = null;
        try {
            res = HttpUtil.HttpRestClient(url, HttpMethod.POST, json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        JSONObject jsonObject = JSON.parseObject(res);
        JSONObject data = jsonObject.getJSONObject("data");
        JSONArray details = data.getJSONArray("details");
        for (int i = 0; i < details.size(); i++) {
            JSONObject jsonObject1 = details.getJSONObject(i);
            String fileId = jsonObject1.getString("file_id");
            String status = jsonObject1.getString("status");

            for (FileEntity file : files) {
                if(fileId.equals(file.getFileId())){
                    FileStatusVO fileStatusVO = new FileStatusVO();
                    fileStatusVO.setId(file.getId());
                    fileStatusVO.setStatus(status);
                    fileStatusList.add(fileStatusVO);
                }
            }
        }
        return fileStatusList;
    }


}
