package com.cqupt.service;

import com.cqupt.domain.dto.FileUploadDTO;
import com.cqupt.domain.dto.KnowledgeBaseDTO;
import com.cqupt.domain.po.KnowledgeBase;
import com.cqupt.domain.vo.FileStatusVO;

import java.io.IOException;
import java.util.List;

public interface KnowledgeBaseService {
    /**
     * 新增知识库
     * @param knowledgeBaseDTO
     * @return
     */
    Integer addKnowledgeBase(KnowledgeBaseDTO knowledgeBaseDTO);

    /**
     * 查询当前用户的知识库列表
     * @param username
     * @return
     */
    List<KnowledgeBase> list(String username);

    /**
     * 根据id删除知识库
     * @param id
     * @return
     */
    Integer deleteById(Long id);

    /**
     * 根据id更新知识库
     * @param knowledgeBaseDTO
     * @return
     */
    Integer updateById(KnowledgeBaseDTO knowledgeBaseDTO);

    /**
     * 上传文件
     * @param knowledgeBaseId
     * @param fileUploadDTOs
     * @return
     * @throws IOException
     */
    Integer uploadFiles(Long knowledgeBaseId, List<FileUploadDTO> fileUploadDTOs) throws IOException;

    /**
     * 删除文件
     * @param knowledgeBaseId
     * @param fileIds
     * @return
     */
    Integer deleteFiles(Long knowledgeBaseId, Long[] fileIds);

    /**
     * 获取某一知识库下的文件状态
     * @param knowledgeBaseId
     * @return
     */
    List<FileStatusVO> getFileStatus(Long knowledgeBaseId);
}
