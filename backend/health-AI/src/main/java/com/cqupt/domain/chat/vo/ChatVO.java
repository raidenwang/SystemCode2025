package com.cqupt.domain.chat.vo;

import com.cqupt.domain.chat.ChatHistoryRecords;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatVO {
    private Long id;
    private ChatHistoryRecords question;
    private ChatHistoryRecords answer;
    private List<ChatSource> chatSourceList;
}
