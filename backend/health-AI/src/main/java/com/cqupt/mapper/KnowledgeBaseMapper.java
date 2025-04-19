package com.cqupt.mapper;

import com.cqupt.domain.po.KnowledgeBase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface KnowledgeBaseMapper {
    void add(KnowledgeBase knowledgeBase);

    KnowledgeBase getByNameAndUserName(@Param("name") String name, @Param("userName") String userName);

    List<KnowledgeBase> list(String username);

    KnowledgeBase getById(Long id);

    void deleteById(Long id);

    void updateById(KnowledgeBase knowledgeBase);
}
