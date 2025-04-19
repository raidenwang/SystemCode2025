package com.cqupt.mapper;

import com.cqupt.domain.po.FileEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface


FileMapper {

    FileEntity getByNameAndCreator(@Param("filename") String filename, @Param("userName") String userName);

    FileEntity getByCondition(@Param("name")String name,
                              @Param("date") LocalDate date,
                              @Param("institution") String institution,
                              @Param("userName") String userName);

    void addBatch(@Param("fileEntities") List<FileEntity> fileEntities);

    List<FileEntity> listByKnowledgeBaseId(Long knowledgeBaseId);

    List<FileEntity> getByIds(@Param("fileIds") Long[] fileIds);

    void deleteByIds(@Param("fileIds") Long[] fileIds);

    void deleteByKnowledgeBaseId(Long id);

    String getInstitutionByFileId(String fileId);
}
