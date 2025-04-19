package com.cqupt.service.chat.impl;

import com.cqupt.domain.chat.ChatPrompt;
import com.cqupt.mapper.chat.ChatPromptMapper;
import com.cqupt.service.chat.IChatPromptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 提示词Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-06-29
 */
@Service
public class ChatPromptServiceImpl implements IChatPromptService
{
    @Autowired
    private ChatPromptMapper chatPromptMapper;

    @Override
    public String getPromptContent(Long id) {
        return chatPromptMapper.getPromptContent(id);
    }

    @Override
    public List<ChatPrompt> selectAllPrompt() {
        return chatPromptMapper.selectAllPrompt();
    }

    /**
     * 查询提示词
     * 
     * @param id 提示词主键
     * @return 提示词
     */
    @Override
    public ChatPrompt selectChatPromptById(Long id)
    {
        return chatPromptMapper.selectChatPromptById(id);
    }

    /**
     * 查询提示词列表
     * 
     * @param chatPrompt 提示词
     * @return 提示词
     */
    @Override
    public List<ChatPrompt> selectChatPromptList(ChatPrompt chatPrompt)
    {
        return chatPromptMapper.selectChatPromptList(chatPrompt);
    }

    /**
     * 新增提示词
     * 
     * @param chatPrompt 提示词
     * @return 结果
     */
    @Override
    public int insertChatPrompt(ChatPrompt chatPrompt)
    {
        return chatPromptMapper.insertChatPrompt(chatPrompt);
    }

    /**
     * 修改提示词
     * 
     * @param chatPrompt 提示词
     * @return 结果
     */
    @Override
    public int updateChatPrompt(ChatPrompt chatPrompt)
    {
        return chatPromptMapper.updateChatPrompt(chatPrompt);
    }

    /**
     * 批量删除提示词
     * 
     * @param ids 需要删除的提示词主键
     * @return 结果
     */
    @Override
    public int deleteChatPromptByIds(Long[] ids)
    {
        return chatPromptMapper.deleteChatPromptByIds(ids);
    }

    /**
     * 删除提示词信息
     * 
     * @param id 提示词主键
     * @return 结果
     */
    @Override
    public int deleteChatPromptById(Long id)
    {
        return chatPromptMapper.deleteChatPromptById(id);
    }
}
