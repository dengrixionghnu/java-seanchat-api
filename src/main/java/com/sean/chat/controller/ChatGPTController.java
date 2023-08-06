package com.sean.chat.controller;

import com.sean.chat.annotation.EnabledOnChatGPT;
import com.sean.chat.misc.Constant;
import com.sean.chat.model.chatgpt.ConversationRequest;
import com.sean.chat.model.chatgpt.FeedbackRequest;
import com.sean.chat.model.chatgpt.GenerateTitleRequest;
import com.sean.chat.model.chatgpt.UpdateConversationRequest;
import com.sean.chat.service.ChatGPTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SuppressWarnings("unused")
@EnabledOnChatGPT
@RestController
public class ChatGPTController {

    @Autowired
    private  ChatGPTService chatGPTService;


    @GetMapping("/conversations")
    public ResponseEntity<String> getConversations(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
            @RequestParam(defaultValue = Constant.DEFAULT_OFFSET) int offset,
            @RequestParam(defaultValue = Constant.DEFAULT_LIMIT) int limit
    ) {
        return chatGPTService.getConversations(accessToken, offset, limit);
    }

    @PostMapping(value = "/conversation")
    public List<String> startConversation(
            @RequestHeader String authorization,
            @RequestBody ConversationRequest conversationRequest
    ) {
        return chatGPTService.startConversation(authorization, conversationRequest);
    }

    @PostMapping("/conversation/gen_title/{conversationId}")
    public ResponseEntity<String> genConversationTitle(
            @RequestHeader String authorization,
            @PathVariable String conversationId,
            @RequestBody GenerateTitleRequest generateTitleRequest
    ) {
        return chatGPTService.genConversationTitle(authorization, conversationId, generateTitleRequest);
    }

    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<String> getConversationContent(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
            @PathVariable String conversationId
    ) {
        return chatGPTService.getConversationContent(accessToken, conversationId);
    }

    @PostMapping("/conversation/{conversationId}")
    public ResponseEntity<Boolean> updateConversation(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
            @PathVariable String conversationId,
            @RequestBody UpdateConversationRequest updateConversationRequest
    ) {
        return chatGPTService.updateConversation(accessToken, conversationId, updateConversationRequest);
    }

    @PostMapping("/conversations")
    public ResponseEntity<Boolean> clearConversations(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
            @RequestBody UpdateConversationRequest updateConversationRequest
    ) {
        return chatGPTService.clearConversations(accessToken, updateConversationRequest);
    }

    @PostMapping("/conversation/message_feedback")
    public ResponseEntity<String> feedbackMessage(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
            @RequestBody FeedbackRequest feedbackRequest
    ) {
        return chatGPTService.feedbackMessage(accessToken, feedbackRequest);
    }

    @GetMapping("/models")
    public ResponseEntity<String> getModels(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken
    ) {
        return chatGPTService.getModels(accessToken);
    }

    @GetMapping("/accounts/check")
    public ResponseEntity<String> checkAccount(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken
    ) {
        return chatGPTService.checkAccount(accessToken);
    }
}
