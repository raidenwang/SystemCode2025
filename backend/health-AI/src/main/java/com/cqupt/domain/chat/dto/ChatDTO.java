package com.cqupt.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatDTO {

    private Long chatId;
    private Long promptId;
    private String content;
    private ArrayList<String> kbIds;
}
