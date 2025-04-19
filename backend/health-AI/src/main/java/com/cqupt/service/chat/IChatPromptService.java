package com.cqupt.service.chat;

import com.cqupt.domain.chat.ChatPrompt;

import java.util.List;

/**
 * 提示词Service接口
 * 
 * @author ruoyi
 * @date 2024-06-29
 */
public interface IChatPromptService
{
    public String getPromptContent(Long id);


    public List<ChatPrompt> selectAllPrompt();

    /**
     * 查询提示词
     * 
     * @param id 提示词主键
     * @return 提示词
     */
    public ChatPrompt selectChatPromptById(Long id);

    /**
     * 查询提示词列表
     * 
     * @param chatPrompt 提示词
     * @return 提示词集合
     */
    public List<ChatPrompt> selectChatPromptList(ChatPrompt chatPrompt);

    /**
     * 新增提示词
     * 
     * @param chatPrompt 提示词
     * @return 结果
     */
    public int insertChatPrompt(ChatPrompt chatPrompt);

    /**
     * 修改提示词
     * 
     * @param chatPrompt 提示词
     * @return 结果
     */
    public int updateChatPrompt(ChatPrompt chatPrompt);

    /**
     * 批量删除提示词
     * 
     * @param ids 需要删除的提示词主键集合
     * @return 结果
     */
    public int deleteChatPromptByIds(Long[] ids);

    /**
     * 删除提示词信息
     * 
     * @param id 提示词主键
     * @return 结果
     */
    public int deleteChatPromptById(Long id);
}
