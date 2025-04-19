package com.cqupt.domain.po;

import lombok.Data;

import java.util.Date;

@Data
public class KnowledgeBase {
    private Long id; //主键id

    private String name; //知识库名称

    private String kbId; //qwen知识库id

    private String creator; //知识库创建者

    private Integer type; //知识库类别，0表示公有public，1表示私有private

    private Date createTime; //创建时间

    private Date updateTime; //更新时间

    private Boolean deleted; //软删除标志
}
