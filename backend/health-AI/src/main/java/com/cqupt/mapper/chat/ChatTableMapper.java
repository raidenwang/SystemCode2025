package com.cqupt.mapper.chat;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqupt.domain.chat.ChatHistoryRecords;
import com.cqupt.domain.chat.ChatTable;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChatTableMapper extends BaseMapper<ChatTable> {

    //将对话的问题或回答保存到历史记录表
    void saveIntoHistory(ChatHistoryRecords historyRecords);

    void add(ChatTable chat);

    @Delete("delete from chat_table where id = #{chatId}")
    void delete(Long chatId);

    void update(ChatTable chat);

    List<ChatTable> getChatIdsByUserId(Long userId);

}
