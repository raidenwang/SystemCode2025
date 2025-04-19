package com.cqupt.service.chat.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cqupt.domain.chat.ChatHistoryRecords;
import com.cqupt.domain.chat.ChatTable;
import com.cqupt.domain.chat.dto.ChatDTO;
import com.cqupt.domain.chat.vo.ChatSource;
import com.cqupt.domain.chat.vo.ChatVO;
import com.cqupt.mapper.FileMapper;
import com.cqupt.mapper.chat.ChatHistoryRecordsMapper;
import com.cqupt.mapper.chat.ChatPromptMapper;
import com.cqupt.mapper.chat.ChatTableMapper;
import com.cqupt.service.chat.ChatTableService;
import com.cqupt.utils.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatTableServiceImpl implements ChatTableService {
    private static final Logger log = LoggerFactory.getLogger(ChatTableServiceImpl.class);
    @Autowired
    private ChatTableMapper chatMapper;


    @Autowired
    private ChatHistoryRecordsMapper historyRecordsMapper;


    @Autowired
    private ChatPromptMapper chatPromptMapper;

    @Value("${chat.url}")
    private String chatURL;

    @Value(value = "${qanything.url}")
    private String qanythingUrl;
    @Autowired
    private FileMapper fileMapper;


    @Override
    public void deleteChatHistory(Long chatId) {
        historyRecordsMapper.deleteByChatId(chatId);
    }

    /**
     *对话
     * @param chatDTO
     * @return
     */
    @Transactional
    public ChatVO chat(ChatDTO chatDTO) {

        //查询历史记录，并将最近的至多8条问答一并发送给算法端 ,若大于8则切片最新的8条记录
        List<ChatHistoryRecords> QAHistoryRecords = historyRecordsMapper.getByChatId(chatDTO.getChatId(), 8);

        //创建HistoryRecords对象
        ChatHistoryRecords question = new ChatHistoryRecords();
        question.setContent(chatDTO.getContent());
        question.setIdClass(0);
        question.setChatId(chatDTO.getChatId());
        question.setCreateTime(LocalDateTime.now());
        question.setUpdateTime(LocalDateTime.now());
        //将本次对话问题保存到历史记录表
        chatMapper.saveIntoHistory(question);
        System.out.println("question:" + JSON.toJSONString(question));
        //如果插件提示词不为空，加上插件提示词
        if(chatDTO.getPromptId() != null) {
            question.setContent(chatPromptMapper.getPromptContent(chatDTO.getPromptId()) + "现在的问题是:" + question.getContent());
        }



        if(chatDTO.getKbIds() == null || chatDTO.getKbIds().isEmpty()){
            //对最近4条历史问答记录进行处理,并存储在HistoryChat字符串中：
            List<String> Ques = new ArrayList<>();
            List<String> HistoryChat = new ArrayList<>();             //为区分问和答，需要多向算法端传递 提问的列表Q
            for (ChatHistoryRecords record : QAHistoryRecords) {
                HistoryChat.add(record.getContent());
                if(record.getIdClass()==0)
                    Ques.add(record.getContent());
            }

            try {
                //post请求
                HttpMethod method =HttpMethod.POST;
                JSONObject json = new JSONObject();
                json.put("history",HistoryChat);
                json.put("content", chatDTO.getContent());
                json.put("questions",Ques);

//            json.put("id", chatDTO.getChatId());

                //解析response并拿到算法返回的数据
                String response = HttpUtil.HttpRestClient(chatURL, method, json);
                JSONObject jsonObject = JSON.parseObject(response);
                String result = jsonObject.getString("response");

                //将本次对话回答保存到历史记录表
                ChatHistoryRecords answer = new ChatHistoryRecords();
                answer.setContent(result);
                answer.setIdClass(1);
                answer.setChatId(chatDTO.getChatId());
                answer.setCreateTime(LocalDateTime.now());
                chatMapper.saveIntoHistory(answer);

                question.setContent(chatDTO.getContent());

                return ChatVO.builder()
                        .id(chatDTO.getChatId())
                        .question(question)
                        .answer(answer)
                        .build();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }else {
            ArrayList<ArrayList<String>> historys = new ArrayList<>();
            //遍历QAHistoryRecords，每两个存储一个为对话记录，放到一个数组中
            for (int i = 0; i < QAHistoryRecords.size(); i += 2) {
                ArrayList<String> history = new ArrayList<>();
                history.add(QAHistoryRecords.get(i).getContent());
                history.add(QAHistoryRecords.get(i + 1).getContent());
                historys.add(history);
            }

//            String username = SecurityUtils.getUsername();
            String url = qanythingUrl + "local_doc_chat";
            JSONObject json = new JSONObject();
            json.put("user_id", "zzp");
            json.put("history", historys);
            json.put("kb_ids", chatDTO.getKbIds());
//            json.put("kb_ids", null);
            json.put("question", question.getContent());

            try {
                String res = HttpUtil.HttpRestClient(url, HttpMethod.POST, json);
                JSONObject jsonObject = JSON.parseObject(res);
                //拿到算法返回的数据
                JSONArray history = jsonObject.getJSONArray("history");
                JSONArray jsonArray = history.getJSONArray(history.size() - 1);
                String result = jsonArray.getString(1);

                //拿到算法返回的参考来源
                JSONArray sources = jsonObject.getJSONArray("source_documents");
                List<ChatSource> chatSources = new ArrayList<>();
                for (int i = 0; i < sources.size(); i++) {
                    JSONObject source = sources.getJSONObject(i);
                    String fileId = source.getString("file_id");
                    String fileName = source.getString("file_name");
                    String content = source.getString("content");
                    String score = source.getString("score");
                    String institution = fileMapper.getInstitutionByFileId(fileId);
                    chatSources.add(new ChatSource(fileId, fileName, content, score, institution));
                }

                //将本次对话回答保存到历史记录表
                ChatHistoryRecords answer = new ChatHistoryRecords();
                answer.setContent(result);
                answer.setIdClass(1);
                answer.setChatId(chatDTO.getChatId());
                answer.setCreateTime(LocalDateTime.now());
                answer.setUpdateTime(LocalDateTime.now());
                if(!chatSources.isEmpty()){
                    answer.setChatSource(JSON.toJSONString(chatSources));
                }

                chatMapper.saveIntoHistory(answer);
                answer.setChatSource(null);

                question.setContent(chatDTO.getContent());
                return ChatVO.builder()
                        .id(chatDTO.getChatId())
                        .question(question)
                        .answer(answer)
                        .chatSourceList(chatSources)
                        .build();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 新增chat
     * @param chatName
     * @return
     */
    public Long addChat(String chatName, Long promptId, Long userId) {
        ChatTable chat = new ChatTable();
        chat.setChatName(chatName);
        if(promptId != -1) {
            chat.setPromptId(promptId);
        }
        chat.setUserId(userId);
        chat.setCreateTime(LocalDateTime.now());
        chatMapper.add(chat);
        return chat.getId();
    }

    /**
     * 删除chat
     * @param chatId
     * @return
     */
    @Transactional
    public void deleteChat(Long chatId) {
        //删除当前chat对应的所有对话历史记录
        historyRecordsMapper.deleteByChatId(chatId);

        //删除当前的chat
        chatMapper.delete(chatId);
    }

    /**
     * 根据chatId获取当前历史记录
     * @param chatId
     * @return
     */
    public List<ChatHistoryRecords> getHistoryRecords(Long chatId) {
        List<ChatHistoryRecords> historyRecordsList = historyRecordsMapper.getByChatId(chatId, null);
        return historyRecordsList;
    }

    /**
     * 修改chat
     * @return
     */
    public void update(ChatTable chat) {
        chat.setUpdateTime(LocalDateTime.now());
        chatMapper.update(chat);
    }

    /**
     * 获取所有的chatId
     * @return
     */
    public List<ChatTable> getAllChatId(Long userId) {
        List<ChatTable> chats = chatMapper.getChatIdsByUserId(userId);
        return chats;
    }

    /**
     * 医学知识库问答
     * @param knowledgeChatDTO
     * @return
     */
//    @Transactional
//    public KnowledgeChatVO knowledgeChat(KnowledgeChatDTO knowledgeChatDTO) {
//        //创建HistoryRecords对象
//        HistoryRecords question = new HistoryRecords();
//        question.setContent(knowledgeChatDTO.getContent());
//        question.setIdClass(0);
//        question.setChatId(knowledgeChatDTO.getChatId());
//        question.setCreateTime(LocalDateTime.now());
//        //将本次对话问题保存到历史记录表
//        chatMapper.saveIntoHistory(question);
//
//        try {
//            //url地址
//            String url = "http://172.20.5.56:30304/PULSE/medical_knowledgebase_chat";
//            //post请求
//            HttpMethod method =HttpMethod.POST;
//            JSONObject json = new JSONObject();
//            json.put("question", knowledgeChatDTO.getContent());
//            String response = HttpUtil.HttpRestClient(url, method, json);
//
//            //解析response并拿到算法返回的数据
//            JSONObject jsonObject = JSON.parseObject(response);
//            String result = jsonObject.getString("response");
//
//            //将本次对话回答保存到历史记录表
//            HistoryRecords answer = new HistoryRecords();
//            answer.setContent(result);
//            answer.setIdClass(1);
//            answer.setChatId(knowledgeChatDTO.getChatId());
//            answer.setCreateTime(LocalDateTime.now());
//            chatMapper.saveIntoHistory(answer);
//
//            return KnowledgeChatVO.builder()
//                    .question(question)
//                    .answer(answer)
//                    .build();
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }



}
