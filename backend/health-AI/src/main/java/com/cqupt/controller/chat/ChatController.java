package com.cqupt.controller.chat;

import com.alibaba.fastjson.JSONArray;
import com.cqupt.common.core.controller.BaseController;
import com.cqupt.common.core.domain.AjaxResult;
import com.cqupt.common.utils.SecurityUtils;
import com.cqupt.common.utils.bean.BeanUtils;
import com.cqupt.domain.chat.ChatHistoryRecords;
import com.cqupt.domain.chat.ChatTable;
import com.cqupt.domain.chat.dto.ChatDTO;
import com.cqupt.domain.chat.vo.ChatHistoryRecordVO;
import com.cqupt.domain.chat.vo.ChatSource;
import com.cqupt.domain.chat.vo.ChatVO;
import com.cqupt.service.chat.ChatTableService;
import com.cqupt.service.chat.IChatPromptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/chat")
@Api(tags = "聊天相关接口")
public class ChatController extends BaseController {

    @Autowired
    private ChatTableService chatService;

    @Autowired
    private IChatPromptService chatPromptService;


    @GetMapping("/getAllPrompt")
    @ApiOperation("获取所有的Prompt")
    public AjaxResult getAllPrompt(){
        return AjaxResult.success(chatPromptService.selectAllPrompt());
    }


    /**
     * 对话
     * @param chatDTO
     * @return
     */
    @PostMapping
    @ApiOperation("对话")
    @Transactional
    public AjaxResult chat(@RequestBody ChatDTO chatDTO){

//        if(chatDTO.getPromptId() == null) {
//            ChatVO chatVO = chatService.chat(chatDTO);
//            return AjaxResult.success("success", chatVO);
//        }
//        else {
//            Long chatId = chatDTO.getChatId();
//            List<ChatHistoryRecords> historyRecords = chatService.getHistoryRecords(chatId);
//
//            //首次聊天
//            if(historyRecords.isEmpty()) {
//                ChatDTO chatDTO1 = new ChatDTO(chatId, null, chatPromptService.getPromptContent(chatDTO.getPromptId()),chatDTO.getKbIds());
//                chatService.chat(chatDTO1);
//
//                ChatTable chatTable = new ChatTable();
//                chatTable.setId(chatId);
//                chatTable.setPromptId(chatDTO.getPromptId());
//                chatService.update(chatTable);
//
//                //提示词问题不用保存
//                chatService.deleteChatHistory(chatId);
//            }
//            chatDTO.setContent(chatPromptService.getPromptContent(chatDTO.getPromptId()) + "现在的问题是:" + chatDTO.getContent());
//            ChatVO chat = chatService.chat(chatDTO);
//
//
//            return AjaxResult.success("success", chat);
//        }
        return AjaxResult.success("success", chatService.chat(chatDTO));
    }

    /**
     * 医学知识库问答
     * @param knowledgeChatDTO
     * @return
     */
//    @PostMapping("/knowledgeChat")
//    @ApiOperation("医学知识库问答")
//    public Result medicalKnowledgeChat(@RequestBody KnowledgeChatDTO knowledgeChatDTO){
//        KnowledgeChatVO knowledgeChatVO = chatService.knowledgeChat(knowledgeChatDTO);
//        return Result.ok(knowledgeChatVO);
//    }

    /**
     * 新增chat
     * @param chatName
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("新增chat")
    public AjaxResult addChat(String chatName, String promptId){
        Long userId = SecurityUtils.getUserId();
        long num = -1;
        try {
            num = Long.parseLong(promptId);
        } catch (NumberFormatException e) {

        }

        Long chatId = chatService.addChat(chatName, num, userId);
        return AjaxResult.success("success", chatId);
//        return Result.ok(chatId).message("success");
    }

    /**
     * 删除chat
     * @param chatId
     * @return
     */
    @DeleteMapping("/delete/{chatId}")
    @ApiOperation("删除chat")
    public AjaxResult deleteChat(@PathVariable Long chatId){
        chatService.deleteChat(chatId);
        return AjaxResult.success();
    }

    /**
     * 根据chatId获取当前历史记录
     * @param chatId
     * @return
     */
    @GetMapping("/getHistoryRecords/{chatId}")
    @ApiOperation("根据chatId获取当前历史记录")
    public AjaxResult getHistoryRecords(@PathVariable Long chatId){
        List<ChatHistoryRecords> historyRecordsList = chatService.getHistoryRecords(chatId);
        List<ChatHistoryRecordVO> chatRecords = new ArrayList<>();
        //封装成VO
        for (ChatHistoryRecords historyRecords : historyRecordsList) {
            ChatHistoryRecordVO chatHistoryRecordVO = new ChatHistoryRecordVO();
            BeanUtils.copyProperties(historyRecords, chatHistoryRecordVO);
            String chatSource = historyRecords.getChatSource();
            // 解析成List<ChatSource>
            chatHistoryRecordVO.setChatSourceList(JSONArray.parseArray(chatSource, ChatSource.class));
            chatRecords.add(chatHistoryRecordVO);
        }
        return AjaxResult.success(chatRecords);
    }

    /**
     * 修改chat
     * @return
     */
    @PutMapping("/update")
    @ApiOperation("修改chat")
    public AjaxResult update(@RequestBody ChatTable chat){
        chatService.update(chat);
        return AjaxResult.success();
    }

    /**
     * 获取所有的chatId
     * @return
     */
    @GetMapping("/getAllChatId")
    @ApiOperation("获取所有的chatId")
    public AjaxResult getAllChatId(){
        Long userId = SecurityUtils.getUserId();

        List<ChatTable> chats = chatService.getAllChatId(userId);
        return AjaxResult.success(chats);
    }
}
