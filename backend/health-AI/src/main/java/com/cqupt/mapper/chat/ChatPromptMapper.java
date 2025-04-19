package com.cqupt.mapper.chat;

import com.cqupt.domain.chat.ChatPrompt;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 提示词Mapper接口
 * 
 * @author ruoyi
 * @date 2024-06-29
 */
@Mapper
public interface ChatPromptMapper 
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
     * 删除提示词
     * 
     * @param id 提示词主键
     * @return 结果
     */
    public int deleteChatPromptById(Long id);

    /**
     * 批量删除提示词
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteChatPromptByIds(Long[] ids);
}
