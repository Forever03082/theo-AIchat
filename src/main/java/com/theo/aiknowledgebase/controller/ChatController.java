package com.theo.aiknowledgebase.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.theo.aiknowledgebase.dto.Result; // 🌟 引入统一返回类
import com.theo.aiknowledgebase.entity.ChatRecord;
import com.theo.aiknowledgebase.entity.Session;
import com.theo.aiknowledgebase.mapper.ChatRecordMapper;
import com.theo.aiknowledgebase.mapper.SessionMapper;
import com.theo.aiknowledgebase.service.OllamaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
public class ChatController {

    @Autowired
    private OllamaService ollamaService;

    @Autowired
    private ChatRecordMapper chatRecordMapper;

    @Autowired
    private SessionMapper sessionMapper;

    /**
     * 1. 规范化：新建会话
     */
    @PostMapping("/session/create")
    public Result<Session> createSession(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            return Result.error(401, "未登录或认证失效");
        }

        Session session = new Session();
        session.setUsername(username);
        session.setTitle("新对话");
        session.setCreateTime(LocalDateTime.now());

        sessionMapper.insert(session); // MyBatis-Plus 会自动将数据库自增的 id 赋值回 session 对象

        // 🌟 关键：用 Result.success 包裹，这样前端才能通过 data.id 拿到自增ID
        return Result.success("创建会话成功", session);
    }

    /**
     * 2. 规范化：删除会话
     */
    @DeleteMapping("/session/{id}")
    public Result<String> deleteSession(@PathVariable Long id) {
        sessionMapper.deleteById(id);
        // 同时删除该会话下的所有聊天记录
        chatRecordMapper.delete(
                new QueryWrapper<ChatRecord>().eq("session_id", id)
        );
        // 🌟 关键：包装成功状态
        return Result.success("删除成功", "ok");
    }

    /**
     * 3. 规范化：获取会话列表
     */
    @GetMapping("/session/list")
    public Result<List<Session>> sessionList(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            return Result.error(401, "未登录或认证失效");
        }

        List<Session> list = sessionMapper.selectList(
                new QueryWrapper<Session>()
                        .eq("username", username)
                        .orderByDesc("create_time")
        );
        // 🌟 关键：包装列表
        return Result.success(list);
    }

    /**
     * 4. 规范化：发送消息（带sessionId）
     */
    @PostMapping("/chat")
    public Result<Map<String, Object>> chat(
            @RequestBody Map<String, String> body,
            HttpServletRequest request
    ) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            return Result.error(401, "未登录或认证失效");
        }

        String message = body.get("message");
        if (body.get("sessionId") == null) {
            return Result.error(400, "会话ID不能为空");
        }
        Long sessionId = Long.parseLong(body.get("sessionId"));

        // 获取历史记录
        List<ChatRecord> history = chatRecordMapper.selectList(
                new QueryWrapper<ChatRecord>()
                        .eq("session_id", sessionId)
                        .orderByAsc("create_time")
        );

        // 构建Ollama请求体
        List<Map<String, Object>> messages = new java.util.ArrayList<>();
        messages.add(Map.of("role", "system", "content", "你是一个知识渊博、乐于助人的AI助手。请用简洁、清晰的语言回答问题。"));

        for (ChatRecord record : history) {
            messages.add(Map.of("role", "user", "content", record.getQuestion()));
            messages.add(Map.of("role", "assistant", "content", record.getAnswer()));
        }
        messages.add(Map.of("role", "user", "content", message));

        // 调用 Ollama
        String answer = ollamaService.chat(messages);

        // 保存记录
        ChatRecord record = new ChatRecord();
        record.setUsername(username);
        record.setSessionId(sessionId);
        record.setQuestion(message);
        record.setAnswer(answer);
        record.setCreateTime(LocalDateTime.now());
        chatRecordMapper.insert(record);

        // 更新标题
        Session session = sessionMapper.selectById(sessionId);
        if (session != null && "新对话".equals(session.getTitle())) {
            session.setTitle(message.length() > 20
                    ? message.substring(0, 20) + "..."
                    : message);
            sessionMapper.updateById(session);
        }

        Map<String, Object> resultMap = Map.of("question", message, "answer", answer);
        // 🌟 关键：包装对话结果
        return Result.success(resultMap);
    }

    /**
     * 5. 规范化：获取某个会话的历史消息
     */
    @GetMapping("/history")
    public Result<List<ChatRecord>> history(
            @RequestParam Long sessionId,
            HttpServletRequest request
    ) {
        List<ChatRecord> list = chatRecordMapper.selectList(
                new QueryWrapper<ChatRecord>()
                        .eq("session_id", sessionId)
                        .orderByAsc("create_time")
        );
        // 🌟 关键：包装历史数据列表
        return Result.success(list);
    }
}