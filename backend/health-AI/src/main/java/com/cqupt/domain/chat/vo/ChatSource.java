package com.cqupt.domain.chat.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatSource {
    private String fileId;
    private String fileName;
    private String content;
    private String score;
    private String institution;
}
