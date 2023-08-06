package com.sean.chat.service;


import com.sean.chat.model.chatgpt.ConversationRequest;
import com.sean.chat.model.chatgpt.FeedbackRequest;
import com.sean.chat.model.chatgpt.GenerateTitleRequest;
import com.sean.chat.model.chatgpt.UpdateConversationRequest;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatGPTService {
    ResponseEntity<String> getConversations(String accessToken, int offset, int limit);

    List<String> startConversation(String accessToken, ConversationRequest conversationRequest);

    ResponseEntity<String> genConversationTitle(
            String accessToken,
            String conversationId,
            GenerateTitleRequest generateTitleRequest
    );

    ResponseEntity<String> getConversationContent(
            String accessToken,
            String conversationId
    );

    ResponseEntity<Boolean> updateConversation(
            String accessToken,
            String conversationId,
            UpdateConversationRequest updateConversationRequest
    );

    ResponseEntity<Boolean> clearConversations(
            String accessToken,
            UpdateConversationRequest updateConversationRequest
    );

    ResponseEntity<String> feedbackMessage(
            String accessToken,
            FeedbackRequest feedbackRequest
    );

    ResponseEntity<String> getModels(
            String accessToken
    );

    ResponseEntity<String> checkAccount(
            String accessToken
    );
}
