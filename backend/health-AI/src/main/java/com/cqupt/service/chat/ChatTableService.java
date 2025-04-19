package com.cqupt.service.chat;

import com.cqupt.domain.chat.ChatHistoryRecords;
import com.cqupt.domain.chat.ChatTable;
import com.cqupt.domain.chat.dto.ChatDTO;
import com.cqupt.domain.chat.vo.ChatVO;

import java.util.List;

public interface ChatTableService {


    void deleteChatHistory(Long chatId);

    /**
     *对话
     * @param chatDTO
     * @return
     */
    ChatVO chat(ChatDTO chatDTO);

    /**
     * 新增chat
     * @param chatName
     * @return
     */
    Long addChat(String chatName, Long promptId, Long userId);

    /**
     * 删除chat
     * @param chatId
     * @return
     */
    void deleteChat(Long chatId);

    /**
     * 根据chatId获取当前历史记录
     * @param chatId
     * @return
     */
    List<ChatHistoryRecords> getHistoryRecords(Long chatId);

    /**
     * 修改chat
     * @return
     */
    void update(ChatTable chat);

    /**
     * 获取所有的chatId
     * @return
     */
    List<ChatTable> getAllChatId(Long userId);

    /**
     * 医学知识库问答
     * @param knowledgeChatDTO
     * @return
     */
//    KnowledgeChatVO knowledgeChat(KnowledgeChatDTO knowledgeChatDTO);
}
