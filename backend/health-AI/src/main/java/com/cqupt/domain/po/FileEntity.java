package com.cqupt.domain.po;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

@Data
public class FileEntity {

    private Long id; //主键id

    private Long knowledgeBaseId; //知识库id，关联知识库表

    private String name; //文档名称

    private String fileId; //qwen知识库文档id

    private String institution; //机构来源

    private LocalDate date; //文档年份

    private String creator; //上传人

    private String tag; //文档相关标签

    private Date createTime; //创建时间

    private Date updateTime; //更新时间

    private Boolean deleted; //软删除标志

//    private String status; //文件状态（red：入库失败-切分失败，green，成功入库，yellow：入库失败-milvus失败，gray：正在入库）


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileEntity that = (FileEntity) o;
        return Objects.equals(name, that.name) && Objects.equals(institution, that.institution) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, institution, date);
    }
}
