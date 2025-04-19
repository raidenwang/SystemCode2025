package com.cqupt.domain.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class FileVO {

    private Long id; //主键id

    private String name; //文档名称

    private String institution; //机构来源

    private String fileId; //qwen知识库文档id

    private LocalDate date; //文档年份

    private String creator; //上传人

    private JSONObject tag; //文档相关标签

    private Date createTime; //创建时间

    private Date updateTime; //更新时间

}
