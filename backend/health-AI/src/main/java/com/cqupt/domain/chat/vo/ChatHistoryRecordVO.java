package com.cqupt.domain.chat.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChatHistoryRecordVO {

    private Long id;
    private Integer idClass;//0表示用户的发送, 1表示算法生成的回答
    private Long chatId;
    private String content;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime createTime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime updateTime;
    private List<ChatSource> chatSourceList;
}
