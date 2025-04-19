package com.cqupt.mapper.chat;

import com.cqupt.domain.chat.ChatHistoryRecords;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatHistoryRecordsMapper {

    void deleteByChatId(Long chatId);

    List<ChatHistoryRecords> getByChatId(@Param("chatId") Long chatId, @Param("num") Integer num);
}
